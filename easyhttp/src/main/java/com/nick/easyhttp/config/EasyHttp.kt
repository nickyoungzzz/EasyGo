package com.nick.easyhttp.config

import com.nick.easyhttp.core.cookie.HttpHandlerCookie
import com.nick.easyhttp.core.req.urlconnection.UrlConnectionClient
import okhttp3.*
import java.net.*
import java.util.concurrent.TimeUnit

object EasyHttp {

	var okHttpClient: OkHttpClient = OkHttpClient()
		private set

	var urlConnectionClient: UrlConnectionClient = UrlConnectionClient()
		private set

	var httpConfig: HttpConfig = HttpConfig.DEFAULT_CONFIG
		private set

	private lateinit var cookieMap: LinkedHashMap<URI, List<HttpHandlerCookie>>

	@Volatile private var hasConfig = false

	@JvmStatic
	@Synchronized fun init(config: HttpConfig) {

		if (hasConfig) throw RuntimeException("Do not config again!!!")

		this.httpConfig = config

		okHttpClient = okHttpClient.newBuilder().proxy(config.proxy)
			.readTimeout(config.readTimeOut, TimeUnit.MILLISECONDS)
			.connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
			.hostnameVerifier(config.hostnameVerifier)
			.sslSocketFactory(config.sslSocketFactory, config.x509TrustManager)
			.dns(object : Dns {
				override fun lookup(hostname: String): List<InetAddress> {
					return httpConfig.dns(hostname).toList()
				}
			})
			.cookieJar(object : CookieJar {
				val cookieHandler = config.httpCookieHandler
				override fun loadForRequest(url: HttpUrl): List<Cookie> {
					return if (!cookieHandler.useCookieForRequest(url.toUri())) emptyList() else run {
						val cookieList = cookieMap[url.toUri()] ?: emptyList()
						cookieList.map { httpHandlerCookie ->
							Cookie.Builder().domain(httpHandlerCookie.domain).name(httpHandlerCookie.name)
								.value(httpHandlerCookie.value).apply { if (httpHandlerCookie.secure) secure() }
								.apply { if (httpHandlerCookie.httpOnly) httpOnly() }
								.path(httpHandlerCookie.path)
								.expiresAt(httpHandlerCookie.whenCreated + httpHandlerCookie.maxAge)
								.hostOnlyDomain(httpHandlerCookie.domain)
								.build()
						}.filter { cookie -> cookie.expiresAt - System.currentTimeMillis() > 0 }
					}
				}

				override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
					val uri = url.toUri()
					val httpHandlerCookies = cookies.map { cookie ->
						HttpHandlerCookie.Builder().whenCreated(System.currentTimeMillis())
							.domain(cookie.domain).name(cookie.name).value(cookie.value)
							.maxAge(cookie.expiresAt - System.currentTimeMillis())
							.httpOnly(cookie.httpOnly)
							.secure(cookie.secure)
							.whenCreated(System.currentTimeMillis())
							.build()
					}.filter { httpHandlerCookie -> cookieHandler.shouldSaveCookie(uri, httpHandlerCookie) }
					synchronized(EasyHttp::class) {
						cookieMap = object : LinkedHashMap<URI, List<HttpHandlerCookie>>() {
							override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, List<HttpHandlerCookie>>?): Boolean {
								return size >= cookieHandler.maxCookieCount(uri)
							}
						}
						cookieMap[uri] = httpHandlerCookies
					}
				}
			})
			.build()

		urlConnectionClient = urlConnectionClient.newBuilder().proxy(config.proxy)
			.readTimeOut(config.readTimeOut)
			.connectTimeOut(config.connectTimeout)
			.hostNameVerifier(config.hostnameVerifier)
			.sslSocketFactory(config.sslSocketFactory)
			.x509TrustManager(config.x509TrustManager)
			.interceptor(config.interceptor)
			.dns(config.dns)
			.build()

		CookieHandler.setDefault(CookieManager(object : CookieStore {

			override fun removeAll(): Boolean {
				cookieMap.clear()
				return cookieMap.size == 0
			}

			override fun add(uri: URI, cookie: HttpCookie) {
				val httpHandlerCookie = httpCookie2HttpHandlerCookie(cookie)
				synchronized(EasyHttp::class) {
					if (cookieMap.containsKey(uri)) {
						cookieMap[uri]?.toMutableList()?.add(httpHandlerCookie)
					} else {
						cookieMap = object : LinkedHashMap<URI, List<HttpHandlerCookie>>() {
							override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, List<HttpHandlerCookie>>?): Boolean {
								return size >= config.httpCookieHandler.maxCookieCount(uri)
							}
						}
						val httpCookieList = ArrayList<HttpHandlerCookie>()
						httpCookieList.add(httpHandlerCookie)
						cookieMap[uri] = httpCookieList
					}
				}
			}

			override fun getCookies(): MutableList<HttpCookie> {
				val list = arrayListOf<HttpCookie>()
				cookieMap.forEach { (_, value) ->
					value.forEach { httpHandlerCookie ->
						val httpCookie = httpHandlerCookie2HttpCookie(httpHandlerCookie)
						list.add(httpCookie)
					}
				}
				return list
			}

			override fun getURIs(): MutableList<URI> {
				return cookieMap.keys.toMutableList()
			}

			override fun remove(uri: URI?, cookie: HttpCookie?): Boolean {
				cookieMap.remove(uri)
				return !cookieMap.containsKey(uri)
			}

			override fun get(uri: URI): MutableList<HttpCookie> {
				val httpCookieList = cookieMap[uri]?.filter { httpHandlerCookie -> System.currentTimeMillis() - httpHandlerCookie.maxAge > 0 }
					?.map { httpHandlerCookie ->
						httpHandlerCookie2HttpCookie(httpHandlerCookie)
					}
				return httpCookieList?.toMutableList() ?: arrayListOf()
			}
		}, CookiePolicy { uri, cookie ->
			val httpHandlerCookie = httpCookie2HttpHandlerCookie(cookie)
			httpConfig.httpCookieHandler.shouldSaveCookie(uri, httpHandlerCookie)
		}))
	}

	private fun httpCookie2HttpHandlerCookie(httpCookie: HttpCookie): HttpHandlerCookie {
		return HttpHandlerCookie.Builder().whenCreated(System.currentTimeMillis())
			.domain(httpCookie.domain).name(httpCookie.name).value(httpCookie.value)
			.maxAge(httpCookie.maxAge + System.currentTimeMillis())
			.secure(httpCookie.secure)
			.whenCreated(System.currentTimeMillis())
			.build()
	}

	private fun httpHandlerCookie2HttpCookie(httpHandlerCookie: HttpHandlerCookie): HttpCookie {
		return HttpCookie(httpHandlerCookie.name, httpHandlerCookie.value).apply {
			secure = httpHandlerCookie.secure
			maxAge = httpHandlerCookie.maxAge
			domain = httpHandlerCookie.domain
			path = httpHandlerCookie.path
		}
	}
}
