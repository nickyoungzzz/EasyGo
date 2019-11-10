package com.nick.lib.network.interfaces

import com.google.gson.Gson
import com.nick.lib.network.HttpProcessorFactory
import com.nick.lib.network.HttpResult
import com.nick.lib.network.util.HttpRequestTagStorage
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.io.File


class HttpDelegate internal constructor(private val reqUrl: String, private val reqMethod: ReqMethod) {

	private var reqTag = ""

	private var queryMap = hashMapOf<String, String>()

	private var headerMap = hashMapOf<String, String>()

	private var fieldMap = hashMapOf<String, String>()

	private val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)

	private var asMultiPart = false

	private var onMainThread = false

	private var jsonString = ""

	private val retrofit = HttpProcessorFactory.retrofit

	fun addQuery(key: String, value: String): HttpDelegate {
		queryMap[key] = value
		return this
	}

	fun addQuerys(queryMap: HashMap<String, String>): HttpDelegate {
		if (queryMap.isNotEmpty()) {
			this.queryMap.forEach {
				if (!this.queryMap.containsKey(it.key)) {
					this.queryMap[it.key] = it.value
				}
			}
		}
		return this
	}

	fun addField(key: String, value: String): HttpDelegate {
		asMultiPart = false
		fieldMap[key] = value
		return this
	}

	fun addFields(fieldMap: HashMap<String, String>): HttpDelegate {
		if (fieldMap.isNotEmpty()) {
			fieldMap.forEach {
				if (!this.fieldMap.containsKey(it.key)) {
					addField(it.key, it.value)
				}
			}
		}
		return this
	}

	fun addJsonString(jsonString: String): HttpDelegate {
		this.jsonString = jsonString
		return this
	}

	fun addMultiPart(key: String, value: Any): HttpDelegate {
		asMultiPart = true
		if (value is String) {
			multipartBody.addFormDataPart(key, value)
		} else if (value is File) {
			val requestBody = value.asRequestBody("Content-Type: application/octet-stream".toMediaTypeOrNull())
			multipartBody.addFormDataPart(key, value.name, requestBody)
		}
		return this
	}

	fun addHeader(key: String, value: String): HttpDelegate {
		headerMap[key] = value
		return this
	}

	fun addHeaders(headerMap: HashMap<String, String>): HttpDelegate {
		if (headerMap.isNotEmpty()) {
			headerMap.forEach {
				if (!this.headerMap.containsKey(it.key)) {
					addHeader(it.key, it.value)
				}
			}
		}
		return this
	}

	fun tag(reqTag: String): HttpDelegate {
		this.reqTag = reqTag
		return this
	}

	fun onMainThread(onMainThread: Boolean): HttpDelegate {
		this.onMainThread = onMainThread
		return this
	}

	fun asMultiPart(multiPart: Boolean): HttpDelegate {
		this.asMultiPart = multiPart
		return this
	}

	fun execute(): Observable<Result<String>> {
		val realHttpUrl = reqUrl.startsWith(HttpProtocol.HTTP.schema, true) or reqUrl.startsWith(HttpProtocol.HTTPS.schema, true)
		val url = if (realHttpUrl) reqUrl else retrofit.baseUrl().toString() + reqUrl
		val httpProcessorService = retrofit.create(HttpProcessorService::class.java)
		val requestBody = jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
		return when (reqMethod) {
			ReqMethod.GET -> httpProcessorService.get(url, headerMap, queryMap)
			ReqMethod.POST -> httpProcessorService.post(url, headerMap, queryMap, requestBody)
			ReqMethod.GET_FORM -> httpProcessorService.getForm(url, headerMap, queryMap, fieldMap)
			ReqMethod.POST_FORM -> httpProcessorService.post(url, headerMap, queryMap,
				if (asMultiPart) multipartBody.build() else constructFormBody())
			ReqMethod.PUT -> httpProcessorService.put(url, headerMap, queryMap, requestBody)
			ReqMethod.DELETE -> httpProcessorService.delete(url, headerMap, queryMap, requestBody)
			ReqMethod.PUT_FORM -> httpProcessorService.put(url, headerMap, queryMap,
				if (asMultiPart) multipartBody.build() else constructFormBody())
			ReqMethod.DELETE_FORM -> httpProcessorService.delete(url, headerMap, queryMap,
				if (asMultiPart) multipartBody.build() else constructFormBody())
		}.subscribeOn(Schedulers.io())
			.unsubscribeOn(Schedulers.io())
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

	fun asString(): HttpResult<*> {
		return asObject(null)
	}

	fun asObject(clazz: Class<*>?): HttpResult<*> {
		return execute().map { t -> toObject(t, clazz) }.blockingFirst()
	}

	fun asList(clazz: Class<*>): HttpResult<*> {
		return execute().map { t -> toList(t, clazz) }.blockingFirst()
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> toList(t: Result<String>, asObjectType: Class<T>): HttpResult<T> {
		if (t.isError) {
			return HttpResult.throwable(t.error())
		} else {
			val response: Response<String>? = t.response()
			val gan = Gson()
			if (response != null) {
				val code = response.code()
				val headers = response.headers()
				if (response.isSuccessful) {
					val result = response.body() as String
					val jsonArray = JSONArray(result)
					val list = arrayListOf<T>()
					for (index in 0..jsonArray.length()) {
						val jsonObject = jsonArray[index]
						list.add(gan.fromJson(jsonObject.toString(), asObjectType))
					}
					return HttpResult.success(list as T, code, headers)
				} else {
					val result = response.errorBody()?.string() as String
					return HttpResult.error(result, code, headers)
				}
			}
			return HttpResult(ResponseStatus.ERROR)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> toObject(t: Result<String>, asObjectType: Class<T>?): HttpResult<T> {
		if (t.isError) {
			return HttpResult.throwable(t.error())
		} else {
			val response: Response<String>? = t.response()
			val gan = Gson()
			if (response != null) {
				val code = response.code()
				val headers = response.headers()
				if (response.isSuccessful) {
					val result = response.body() as String
					val success = if (asObjectType == String::class.java || asObjectType == null) result as T else gan.fromJson(result, asObjectType)
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
		execute().subscribe(object : Observer<Result<String>> {
			override fun onComplete() {
				HttpRequestTagStorage.cancelRequestTag(reqTag)
			}

			override fun onSubscribe(d: Disposable) {
				httpCallBack.onResult(HttpResult.loading())
				HttpRequestTagStorage.addRequestTag(reqTag, d)
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
}