package com.nick.lib.network

import com.google.gson.Gson
import com.nick.lib.network.interceptor.HeaderInterceptor
import com.nick.lib.network.interceptor.QueryInterceptor
import com.nick.lib.network.interfaces.HttpCallBack
import com.nick.lib.network.interfaces.HttpProcessorService
import com.nick.lib.network.interfaces.ReqMethod
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
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

	class HttpDelegate internal constructor(var url: String, var reqMethod: ReqMethod) {

		private var reqTag = ""

		private var queryMap = hashMapOf<String, String>()

		private var headerMap = hashMapOf<String, String>()

		private var fieldMap = hashMapOf<String, String>()

		private var onMainThread = true

		private var jsonString = ""

		private val retrofitBuilder = Retrofit.Builder().baseUrl("https://www.baidu.com/")
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(ScalarsConverterFactory.create())

		private var okHttpClient: OkHttpClient? = null

		private var okHttpClientBuilder = OkHttpClient.Builder()
			.sslSocketFactory(SslHelper.getSSLSocketFactory(), SslHelper.getTrustManager())
			.hostnameVerifier(SslHelper.getHostnameVerifier())
			.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
			.readTimeout(TIMEOUT, TimeUnit.SECONDS)
			.connectTimeout(TIMEOUT, TimeUnit.SECONDS)

		fun addQuery(key: String, value: String): HttpDelegate {
			queryMap[key] = value
			return this
		}

		fun addQueryMap(map: HashMap<String, String>): HttpDelegate {
			okHttpClientBuilder.addInterceptor(QueryInterceptor(map))
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
						this.fieldMap[it.key] = it.value
					}
				}
			}
			return this
		}

		fun addJsonString(jsonString: String): HttpDelegate {
			this.jsonString = jsonString
			return this
		}

		fun addHeader(key: String, value: String): HttpDelegate {
			headerMap[key] = value
			return this
		}

		fun addHeaders(headerMap: HashMap<String, String>): HttpDelegate {
			okHttpClientBuilder.addInterceptor(HeaderInterceptor(headerMap))
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

		fun client(client: OkHttpClient): HttpDelegate {
			this.okHttpClient = client
			return this
		}

		fun onMainThread(onMainThread: Boolean): HttpDelegate {
			this.onMainThread = onMainThread
			return this
		}

		fun timeOut(timeOut: Long, timeUnit: TimeUnit): HttpDelegate {
			okHttpClientBuilder.writeTimeout(timeOut, timeUnit)
				.readTimeout(timeOut, timeUnit)
				.connectTimeout(timeOut, timeUnit)
			return this
		}

		@Suppress("UNCHECKED_CAST") fun <T, F> process(httpCallBack: HttpCallBack<T, F>) {

			val httpProcessorService = retrofitBuilder.client(okHttpClient ?: okHttpClientBuilder.build())
				.build().create(HttpProcessorService::class.java)
			val observableResult = when (reqMethod) {
				ReqMethod.GET ->
					httpProcessorService.get(url, headerMap, queryMap)
				ReqMethod.POST -> {
					val requestBody = RequestBody.create(MediaType.parse("Content-Type:application/json;charset=utf-8"), jsonString)
					httpProcessorService.post(url, headerMap, queryMap, requestBody)
				}
				ReqMethod.GET_FORM -> httpProcessorService.getForm(url, headerMap, queryMap, fieldMap)
				ReqMethod.POST_FORM -> httpProcessorService.postForm(url, headerMap, queryMap, fieldMap)
				ReqMethod.PUT -> httpProcessorService.put(url, headerMap, queryMap)
			}
			observableResult.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.observeOn(if (onMainThread) AndroidSchedulers.mainThread() else Schedulers.io())
				.subscribe(object : Observer<Result<String>> {
					override fun onComplete() {
						reqTagMap.remove(reqTag)
					}

					override fun onSubscribe(d: Disposable) {
						httpCallBack.onLoading()
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