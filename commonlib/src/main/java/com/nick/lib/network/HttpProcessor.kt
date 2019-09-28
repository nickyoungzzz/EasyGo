package com.nick.lib.network

import com.google.gson.Gson
import com.nick.lib.BuildConfig
import com.nick.lib.network.interceptor.HeaderInterceptor
import com.nick.lib.network.interfaces.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class HttpProcessor {

	companion object {

		@JvmStatic
		fun get(url: String) = HttpDelegate(url, ReqMethod.GET)

		@JvmStatic
		fun post(url: String) = HttpDelegate(url, ReqMethod.POST)

		@JvmStatic
		fun getForm(url: String) = HttpDelegate(url, ReqMethod.GET_FORM)

		@JvmStatic
		fun postForm(url: String) = HttpDelegate(url, ReqMethod.POST_FORM)

		@JvmStatic
		fun put(url: String) = HttpDelegate(url, ReqMethod.PUT)

		private val reqTagMap = hashMapOf<String, CompositeDisposable>()

		fun clear(tag: String) {
			val compositeDisposable: CompositeDisposable? = reqTagMap[tag]
			compositeDisposable?.clear()
		}

		const val TIMEOUT = 30L
	}

	class HttpDelegate internal constructor(private val url: String, private val reqMethod: ReqMethod) {

		private var reqTag = ""

		private var queryMap = hashMapOf<String, String>()

		private var headerMap = hashMapOf<String, String>()

		private var fieldMap = hashMapOf<String, String>()

		private val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)

		private var asMultiPart = false

		private var onMainThread = true

		private var jsonString = ""

		private val retrofitBuilder = Retrofit.Builder()
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(ScalarsConverterFactory.create())

		private var okHttpClientBuilder = OkHttpClient.Builder()
			.sslSocketFactory(SslHelper.getSSLSocketFactory(), SslHelper.getTrustManager())
			.hostnameVerifier(SslHelper.getHostnameVerifier())
			.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
			.readTimeout(TIMEOUT, TimeUnit.SECONDS)
			.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
			.addInterceptor(run {
				val httpLoggingInterceptor = HttpLoggingInterceptor()
				httpLoggingInterceptor.apply {
					this.level = if (BuildConfig.DEBUG)
						HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
				}
			})

		fun baseUrl(baseUrl: String): HttpDelegate {
			retrofitBuilder.baseUrl(baseUrl)
			return this
		}

		fun addQuery(key: String, value: String): HttpDelegate {
			queryMap[key] = value
			return this
		}

		fun addQueryMap(queryMap: HashMap<String, String>): HttpDelegate {
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
				val file = value as File
				val requestBody = file.asRequestBody("Content-Type: application/octet-stream".toMediaTypeOrNull())
				multipartBody.addFormDataPart(key, file.name, requestBody)
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

		fun addInterceptor(interceptor: Interceptor): HttpDelegate {
			okHttpClientBuilder.addInterceptor(interceptor)
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

		fun asMultiPart(): HttpDelegate {
			this.asMultiPart = true
			return this
		}

		fun timeOut(timeOut: Long, timeUnit: TimeUnit): HttpDelegate {
			okHttpClientBuilder.writeTimeout(timeOut, timeUnit)
				.readTimeout(timeOut, timeUnit)
				.connectTimeout(timeOut, timeUnit)
			return this
		}

		fun config(httpConfig: HttpConfig): HttpDelegate {
			retrofitBuilder.baseUrl(httpConfig.baseUrl())
			okHttpClientBuilder.addInterceptor(HeaderInterceptor(httpConfig.headers()))
			return this
		}

		fun defaultConfig(): HttpDelegate {
			this.config(DefaultConfig.create())
			return this
		}

		@Suppress("UNCHECKED_CAST") fun <T, F> process(httpCallBack: HttpCallBack<T, F>) {
			val httpUrl = url.startsWith(HttpProtocol.HTTP.schema, true) or url.startsWith(HttpProtocol.HTTPS.schema, true)
			val httpProcessorService = retrofitBuilder.client(okHttpClientBuilder.build())
				.build().create(HttpProcessorService::class.java)
			val observableResult = when (reqMethod) {
				ReqMethod.GET -> if (httpUrl)
					httpProcessorService.getByUrl(url, headerMap, queryMap)
				else
					httpProcessorService.get(url, headerMap, queryMap)
				ReqMethod.POST -> {
					val requestBody = jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
					if (httpUrl)
						httpProcessorService.postByUrl(url, headerMap, queryMap, requestBody)
					else
						httpProcessorService.post(url, headerMap, queryMap, requestBody)
				}
				ReqMethod.GET_FORM ->
					if (httpUrl)
						httpProcessorService.getFormByUrl(url, headerMap, queryMap, fieldMap)
					else
						httpProcessorService.getForm(url, headerMap, queryMap, fieldMap)
				ReqMethod.POST_FORM -> {
					if (asMultiPart) {
						if (httpUrl)
							httpProcessorService.postByUrl(url, headerMap, queryMap, multipartBody.build())
						else
							httpProcessorService.post(url, headerMap, queryMap, multipartBody.build())
					} else {
						if (httpUrl)
							httpProcessorService.postFormByUrl(url, headerMap, queryMap, fieldMap)
						else
							httpProcessorService.postForm(url, headerMap, queryMap, fieldMap)
					}
				}
				ReqMethod.PUT ->
					if (httpUrl)
						httpProcessorService.putByUrl(url, headerMap, queryMap)
					else
						httpProcessorService.putByUrl(url, headerMap, queryMap)
			}
			observableResult.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.observeOn(if (onMainThread) AndroidSchedulers.mainThread() else Schedulers.io())
				.subscribe(object : Observer<Result<String>> {
					override fun onComplete() {
						reqTagMap.remove(reqTag)
					}

					override fun onSubscribe(d: Disposable) {
						httpCallBack.onResult(HttpResult.loading())
						if (reqTag.isNotEmpty()) {
							if (reqTagMap.containsKey(reqTag)) {
								val compositeDisposable = reqTagMap[reqTag]
								compositeDisposable?.add(d)
							} else {
								val compositeDisposable = CompositeDisposable(d)
								reqTagMap[reqTag] = compositeDisposable
							}
						}
					}

					override fun onNext(t: Result<String>) {
						if (t.isError) {
							httpCallBack.onResult(HttpResult.throwable(t.error()))
						} else {
							val res: Response<String>? = t.response()
							if (res != null) {
								val response = res as Response<String>
								val code = response.code()
								val headers = response.headers()
								val type = httpCallBack.getGenericType(if (response.isSuccessful) 0 else 1)
								val result = if (response.isSuccessful) response.body() else response.errorBody()?.string()
								if (response.isSuccessful) {
									httpCallBack.onResult(HttpResult.success(if (type == String::class.java)
										result as T else Gson().fromJson(result, type), code, headers))
								} else {
									httpCallBack.onResult(HttpResult.error(if (type == String::class.java)
										result as F else Gson().fromJson(result, type), code, headers))
								}
							}
						}
					}

					override fun onError(e: Throwable) {
						httpCallBack.onResult(HttpResult.throwable(e))
					}
				})
		}

	}
}