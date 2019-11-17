package com.nick.lib.network.interfaces

import com.google.gson.Gson
import com.nick.lib.network.HttpConfigFactory
import com.nick.lib.network.HttpResult
import com.nick.lib.network.HttpThrowable
import com.nick.lib.network.util.parseAsList
import com.nick.lib.network.util.parseAsObject
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.io.File

class HttpRequest internal constructor(private val reqUrl: String, private val reqMethod: ReqMethod) {

	private var reqTag: Any? = null

	private var queryMap = hashMapOf<String, String>()

	private var headerMap = hashMapOf<String, String>()

	private var fieldMap = hashMapOf<String, String>()

	private val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)

	private var isMultiPart = false

	private var onMainThread = false

	private var jsonString = ""

	private val retrofit by lazy {
		HttpConfigFactory.retrofit
			?: throw RuntimeException("please config EasyHttp first!!!")
	}

	fun addQuery(key: String, value: String): HttpRequest {
		queryMap[key] = value
		return this
	}

	fun addQueries(queryMap: HashMap<String, String>): HttpRequest {
		if (queryMap.isNotEmpty()) {
			this.queryMap.forEach {
				if (!this.queryMap.containsKey(it.key)) {
					this.queryMap[it.key] = it.value
				}
			}
		}
		return this
	}

	fun addField(key: String, value: String): HttpRequest {
		isMultiPart = false
		fieldMap[key] = value
		return this
	}

	fun addFields(fieldMap: HashMap<String, String>): HttpRequest {
		if (fieldMap.isNotEmpty()) {
			fieldMap.forEach {
				if (!this.fieldMap.containsKey(it.key)) {
					addField(it.key, it.value)
				}
			}
		}
		return this
	}

	fun addJsonString(jsonString: String): HttpRequest {
		this.jsonString = jsonString
		return this
	}

	fun addMultiPart(key: String, value: Any): HttpRequest {
		isMultiPart = true
		if (value is String) {
			multipartBody.addFormDataPart(key, value)
		} else if (value is File) {
			val requestBody = value.asRequestBody("Content-Type: application/octet-stream".toMediaTypeOrNull())
			multipartBody.addFormDataPart(key, value.name, requestBody)
		}
		return this
	}

	fun addHeader(key: String, value: String): HttpRequest {
		headerMap[key] = value
		return this
	}

	fun addHeaders(headerMap: HashMap<String, String>): HttpRequest {
		if (headerMap.isNotEmpty()) {
			headerMap.forEach {
				if (!this.headerMap.containsKey(it.key)) {
					addHeader(it.key, it.value)
				}
			}
		}
		return this
	}

	fun tag(reqTag: Any): HttpRequest {
		this.reqTag = reqTag
		return this
	}

	fun onMainThread(onMainThread: Boolean): HttpRequest {
		this.onMainThread = onMainThread
		return this
	}

	fun isMultiPart(multiPart: Boolean): HttpRequest {
		this.isMultiPart = multiPart
		return this
	}

	private fun request(): Observable<Result<String>> {
		val realHttpUrl = reqUrl.startsWith(HttpProtocol.HTTP.schema, true) or reqUrl.startsWith(HttpProtocol.HTTPS.schema, true)
		val url = if (realHttpUrl) reqUrl else retrofit.baseUrl().toString().plus(reqUrl)
		val httpProcessorService = retrofit.create(HttpProcessorService::class.java)
		val requestBody = jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
		return when (reqMethod) {
			ReqMethod.GET -> httpProcessorService.get(url, headerMap, queryMap, reqTag)
			ReqMethod.POST -> httpProcessorService.post(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.GET_FORM -> httpProcessorService.getForm(url, headerMap, queryMap, fieldMap, reqTag)
			ReqMethod.POST_FORM -> httpProcessorService.post(url, headerMap, queryMap,
				if (isMultiPart) multipartBody.build() else constructFormBody(), reqTag)
			ReqMethod.PUT -> httpProcessorService.put(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.DELETE -> httpProcessorService.delete(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.PUT_FORM -> httpProcessorService.put(url, headerMap, queryMap,
				if (isMultiPart) multipartBody.build() else constructFormBody(), reqTag)
			ReqMethod.DELETE_FORM -> httpProcessorService.delete(url, headerMap, queryMap,
				if (isMultiPart) multipartBody.build() else constructFormBody(), reqTag)
		}.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
			.observeOn(if (onMainThread) AndroidSchedulers.mainThread() else Schedulers.io())
	}

	private fun constructFormBody(): FormBody {
		val formBodyBuilder = FormBody.Builder()
		if (!fieldMap.isNullOrEmpty()) {
			fieldMap.forEach {
				formBodyBuilder.addEncoded(it.key, it.value)
			}
		}
		return formBodyBuilder.build()
	}


	fun asString(): HttpResult<String> {
		return asObject(String::class.java)
	}

	fun <T> asObject(clazz: Class<T>): HttpResult<T> {
		return request().map { t -> toObject(t, clazz) }.blockingFirst()
	}

	fun <T> asList(clazz: Class<T>): HttpResult<MutableList<T>> {
		return request().map { t -> toList(t, clazz) }.blockingFirst()
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> toList(t: Result<String>, asObjectType: Class<T>): HttpResult<MutableList<T>> {
		if (t.isError) {
			return HttpResult.throwable(t.error())
		} else {
			val response: Response<String>? = t.response()
			if (response != null) {
				val code = response.code()
				val headers = response.headers()
				if (response.isSuccessful) {
					val result = response.body() as String
					val list = result.parseAsList(asObjectType)
					return HttpResult.success(list, code, headers)
				} else {
					val result = response.errorBody()?.string() as String
					return HttpResult.error(result, code, headers)
				}
			}
			return HttpResult(ResponseStatus.ERROR)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> toObject(t: Result<String>, asObjectType: Class<T>): HttpResult<T> {
		if (t.isError) {
			return HttpResult.throwable(t.error())
		} else {
			val response: Response<String>? = t.response()
			if (response != null) {
				val code = response.code()
				val headers = response.headers()
				if (response.isSuccessful) {
					val result = response.body() as String
					val success = if (asObjectType == String::class.java) result as T else result.parseAsObject(asObjectType)
					return HttpResult.success(success, code, headers)
				} else {
					val result = response.errorBody()?.string() as String
					return HttpResult.error(result, code, headers)
				}
			}
			return HttpResult(ResponseStatus.ERROR)
		}
	}

	@Suppress("UNCHECKED_CAST") fun <T> enqueue(httpCallBack: HttpCallBack<T>) {
		request().subscribe(object : Observer<Result<String>> {
			override fun onComplete() {
			}

			override fun onSubscribe(d: Disposable) {
				httpCallBack.onResult(HttpResult.loading())
			}

			override fun onNext(t: Result<String>) {
				if (t.isError) {
					httpCallBack.onResult(HttpResult.throwable(t.error()))
				} else {
					val response: Response<String>? = t.response()
					val gan = Gson()
					if (response != null) {
						val code = response.code()
						val headers = response.headers()
						val type = httpCallBack.getGenericType(0)
						if (response.isSuccessful) {
							val result = response.body() as String
							val success = if (type == String::class.java) result as T else gan.fromJson(result, type)
							httpCallBack.onResult(HttpResult.success(success, code, headers))
						} else {
							val result = response.errorBody()?.string() as String
							httpCallBack.onResult(HttpResult.error(result, code, headers))
						}
					} else {
						httpCallBack.onResult(HttpResult(ResponseStatus.ERROR))
					}
				}
			}

			override fun onError(e: Throwable) {
				httpCallBack.onResult(HttpResult.throwable(e))
			}
		})
	}

	fun execute(): Observable<HttpResult<String>> {
		return request().flatMap { t ->
			Observable.create { emitter: ObservableEmitter<HttpResult<String>> ->
				if (t.isError) {
					emitter.onError(HttpThrowable(t.error()!!))
				} else {
					val response: Response<String>? = t.response()
					if (response != null) {
						val code = response.code()
						val headers = response.headers()
						if (response.isSuccessful) {
							val result = response.body() as String
							emitter.onNext(HttpResult.success(result, code, headers))
						} else {
							val result = response.errorBody()?.string() as String
							val throwable = HttpThrowable(Throwable(result), code)
							emitter.onError(throwable)
						}
					}
				}
			}
		}
	}
}