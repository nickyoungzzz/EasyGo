package com.nick.easygo.config

import com.nick.easygo.core.HttpCacheStrategy
import com.nick.easygo.core.ReqMethod
import com.nick.easygo.core.cache.HttpCache
import com.nick.easygo.core.cookie.HttpCookie
import com.nick.easygo.core.req.urlconnection.UrlConnectionClient
import okhttp3.*
import java.io.*
import java.net.*
import java.util.concurrent.TimeUnit

object EasyGo {

	internal lateinit var okHttpClient: OkHttpClient
		private set

	internal lateinit var urlConnectionClient: UrlConnectionClient
		private set

	internal lateinit var httpConfig: HttpConfig
		private set

	private lateinit var cookieMap: LinkedHashMap<URI, List<HttpCookie>>

	private lateinit var cacheMap: LinkedHashMap<URI, HttpCache>

	private lateinit var cacheFile: File

	@Volatile
	private var hasConfig = false

	@Volatile
	private var isMemoryCache = true

	@JvmStatic
	@Synchronized
	fun initialize(config: HttpConfig.Builder.() -> Unit = {}) {
		if (hasConfig) throw RuntimeException("Do not config again!!!")
		this.httpConfig = HttpConfig.Builder().apply(config).build()
		hasConfig = true

		cookieMap = object : LinkedHashMap<URI, List<HttpCookie>>() {
			override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, List<HttpCookie>>?): Boolean {
				return size >= httpConfig.httpCookieHandler.maxCookieCount()
			}
		}

		isMemoryCache = httpConfig.httpCacheHandler.cacheStrategy == HttpCacheStrategy.MEMORY_CACHE
		if (isMemoryCache) {
			cacheMap = object : LinkedHashMap<URI, HttpCache>() {
				override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, HttpCache>?): Boolean {
					return size >= httpConfig.httpCacheHandler.cacheCount
				}
			}
		} else {
			cacheFile = httpConfig.httpCacheHandler.fileCache
		}

		okHttpClient = OkHttpClient.Builder().proxy(httpConfig.proxy)
			.readTimeout(httpConfig.readTimeOut, TimeUnit.MILLISECONDS)
			.connectTimeout(httpConfig.connectTimeout, TimeUnit.MILLISECONDS)
			.hostnameVerifier(httpConfig.hostnameVerifier)
			.sslSocketFactory(httpConfig.sslSocketFactory, httpConfig.x509TrustManager)
			.dns(object : Dns {
				override fun lookup(hostname: String): List<InetAddress> {
					return httpConfig.dns(hostname).toList()
				}
			})
			.cookieJar(object : CookieJar {
				val cookieHandler = httpConfig.httpCookieHandler
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
						HttpCookie.Builder().whenCreated(System.currentTimeMillis())
							.domain(cookie.domain).name(cookie.name).value(cookie.value)
							.maxAge(cookie.expiresAt - System.currentTimeMillis())
							.httpOnly(cookie.httpOnly)
							.secure(cookie.secure)
							.whenCreated(System.currentTimeMillis())
							.build()
					}.filter { httpHandlerCookie -> cookieHandler.shouldSaveCookie(uri, httpHandlerCookie) }
					synchronized(EasyGo::class) {
						cookieMap[uri] = httpHandlerCookies.apply {
							val eachUriCookieCount = cookieHandler.eachUriCookieCount(uri)
							if (this.size >= eachUriCookieCount) {
								subList(this.size - eachUriCookieCount, this.size)
							}
						}
					}
				}
			}).addInterceptor(object : Interceptor {
				override fun intercept(chain: Interceptor.Chain): Response {
					val request = chain.request()
					val timeoutConfig = httpConfig.timeoutHandler(request.url.toUri().toString(), request.tag(),
						request.method, request.headers.toMultimap())
					return chain.withConnectTimeout(timeoutConfig.connectTimeout.toInt(), TimeUnit.MILLISECONDS)
						.withReadTimeout(timeoutConfig.readTimeOut.toInt(), TimeUnit.MILLISECONDS)
						.withWriteTimeout(timeoutConfig.writeTimeOut.toInt(), TimeUnit.MILLISECONDS)
						.proceed(request)
				}
			})
			.build()

		urlConnectionClient = UrlConnectionClient.Builder().proxy(httpConfig.proxy)
			.readTimeOut(httpConfig.readTimeOut)
			.connectTimeOut(httpConfig.connectTimeout)
			.hostNameVerifier(httpConfig.hostnameVerifier)
			.sslSocketFactory(httpConfig.sslSocketFactory)
			.x509TrustManager(httpConfig.x509TrustManager)
			.dns(httpConfig.dns)
			.build().apply {
				setInterceptor { urlConnectionReq ->
					val headerMap = hashMapOf<String, List<String>>()
					urlConnectionReq.headerMap.forEach { (key, value) ->
						if (headerMap.containsKey(key)) {
							val list = headerMap[key] as ArrayList
							list.add(value)
						} else {
							val list = arrayListOf<String>()
							list.add(value)
							headerMap[key] = list
						}
					}
					val timeoutConfig = httpConfig.timeoutHandler(urlConnectionReq.url, urlConnectionReq.reqTag,
						urlConnectionReq.reqMethod.method, headerMap)
					return@setInterceptor this.proceedInternal(urlConnectionReq.newBuilder()
						.connectTimeOut(timeoutConfig.connectTimeout).readTimeOut(timeoutConfig.readTimeOut)
						.writeTimeOut(timeoutConfig.writeTimeOut).build())
				}
			}

		CookieHandler.setDefault(CookieManager(object : CookieStore {

			override fun removeAll(): Boolean {
				cookieMap.clear()
				return cookieMap.isEmpty()
			}

			override fun add(uri: URI, cookie: java.net.HttpCookie) {
				val httpHandlerCookie = httpCookie2HttpHandlerCookie(cookie)
				val eachUriCookieCount = httpConfig.httpCookieHandler.eachUriCookieCount(uri)
				synchronized(EasyGo::class) {
					if (cookieMap.containsKey(uri)) {
						val httpCookieList = cookieMap[uri]?.toMutableList() ?: arrayListOf()
						httpCookieList.add(httpHandlerCookie)
						if (httpCookieList.size >= eachUriCookieCount) {
							httpCookieList.subList(httpCookieList.size - eachUriCookieCount, httpCookieList.size)
						}
					} else {
						val httpCookieList = ArrayList<HttpCookie>(eachUriCookieCount)
						httpCookieList.add(httpHandlerCookie)
						cookieMap[uri] = httpCookieList
					}
				}
			}

			override fun getCookies(): MutableList<java.net.HttpCookie>? {
				val list = arrayListOf<java.net.HttpCookie>()
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

			override fun remove(uri: URI?, cookie: java.net.HttpCookie?): Boolean {
				cookieMap.remove(uri)
				return !cookieMap.containsKey(uri)
			}

			override fun get(uri: URI): MutableList<java.net.HttpCookie>? {
				val httpCookieList = cookieMap[uri]?.filter { httpHandlerCookie -> System.currentTimeMillis() - httpHandlerCookie.maxAge > 0 }
					?.map { httpHandlerCookie ->
						httpHandlerCookie2HttpCookie(httpHandlerCookie)
					}
				return httpCookieList?.toMutableList() ?: arrayListOf()
			}
		}) { uri, cookie ->
			val httpHandlerCookie = httpCookie2HttpHandlerCookie(cookie)
			httpConfig.httpCookieHandler.shouldSaveCookie(uri, httpHandlerCookie)
		})

		ResponseCache.setDefault(object : ResponseCache() {
			lateinit var objectInputStream: ObjectInputStream
			lateinit var objectOutputStream: ObjectOutputStream

			override fun put(uri: URI, conn: URLConnection): CacheRequest? {
				val cacheControlHeaders = conn.headerFields["Cache-Control"] ?: emptyList()
				val cacheRequest = object : CacheRequest() {
					override fun getBody(): OutputStream {
						return ByteArrayOutputStream()
					}

					override fun abort() {
					}
				}
				val cacheResponse = object : CacheResponse() {
					override fun getHeaders(): MutableMap<String, MutableList<String>> {
						return conn.headerFields
					}

					override fun getBody(): InputStream {
						return conn.getInputStream()
					}
				}
				val cache: LinkedHashMap<URI, HttpCache> = if (isMemoryCache) cacheMap else {
					run {
						try {
							objectInputStream = ObjectInputStream(FileInputStream(cacheFile))
							@Suppress("UNCHECKED_CAST")
							objectInputStream.readObject() as LinkedHashMap<URI, HttpCache>
						} catch (e: IOException) {
							return null
						}
					}
				}
				val cacheBuilder = HttpCache.Builder()
				val httpCache = cacheBuilder.cacheCreated(System.currentTimeMillis()).cacheResponse(cacheResponse)
					.apply {
						cacheControlHeaders.forEach { t ->
							if (t.startsWith("max-age", true)) {
								val maxAge = t.substring(8).toLong()
								maxAge(maxAge)
							}
							if (t.equals("no-store", true)) {
								noStore()
								cache.remove(uri)
							}
							if (t.equals("no-cache", true)) {
								noCache()
								cache.remove(uri)
							}
							if (t.equals("public", true)) {
								isPublic()
							}
							if (t.equals("private", true)) {
								isPrivate()
							}
							if (t.equals("must-revalidate", true)) {
								mustRevalidate()
							}
							if (t.startsWith("max-stale", true)) {
								val maxStale = t.substring(10).toLong()
								maxStale(maxStale)
							}
						}
					}.build()

				if (httpCache.maxAge > 0 && !httpCache.noCache && !httpCache.noStore) {
					if (isMemoryCache) {
						cacheMap[uri] = httpCache
					} else {
						val c: LinkedHashMap<URI, HttpCache> = object : LinkedHashMap<URI, HttpCache>() {
							override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, HttpCache>?): Boolean {
								return size >= httpConfig.httpCacheHandler.cacheCount
							}
						}
						c[uri] = httpCache
						objectOutputStream = ObjectOutputStream(FileOutputStream(cacheFile))
						objectOutputStream.writeObject(c)
						objectInputStream.close()
					}
				} else {
					cache.remove(uri)
				}
				return cacheRequest
			}

			override fun get(uri: URI, rqstMethod: String, rqstHeaders: MutableMap<String, MutableList<String>>): CacheResponse? {
				val cacheControlHeaders = rqstHeaders["Cache-Control"] ?: arrayListOf()
				val rule = rqstMethod == ReqMethod.GET.method && cacheControlHeaders.contains("only-if-cached")
				val cache: LinkedHashMap<URI, HttpCache> = if (isMemoryCache) cacheMap else {
					run {
						try {
							objectInputStream = ObjectInputStream(FileInputStream(cacheFile))
							@Suppress("UNCHECKED_CAST")
							objectInputStream.readObject() as LinkedHashMap<URI, HttpCache>
						} catch (e: IOException) {
							return null
						} finally {
							objectInputStream.close()
						}
					}
				}
				if (cache.containsKey(uri)) {
					val httpCache = cache[uri]!!
					if (rule && System.currentTimeMillis() - httpCache.cacheCreated < httpCache.maxAge) {
						return httpCache.cacheResponse
					}
				}
				return null
			}
		})
	}

	private fun httpCookie2HttpHandlerCookie(httpCookie: java.net.HttpCookie): HttpCookie {
		return HttpCookie.Builder().whenCreated(System.currentTimeMillis())
			.domain(httpCookie.domain).name(httpCookie.name).value(httpCookie.value)
			.maxAge(httpCookie.maxAge + System.currentTimeMillis())
			.secure(httpCookie.secure)
			.whenCreated(System.currentTimeMillis())
			.build()
	}

	private fun httpHandlerCookie2HttpCookie(httpCookie: HttpCookie): java.net.HttpCookie {
		return HttpCookie(httpCookie.name, httpCookie.value).apply {
			secure = httpCookie.secure
			maxAge = httpCookie.maxAge
			domain = httpCookie.domain
			path = httpCookie.path
		}
	}

	fun getAllCookies(): LinkedHashMap<URI, List<HttpCookie>> {
		return cookieMap
	}

	fun getAllCaches(): LinkedHashMap<URI, HttpCache> {
		@Suppress("UNCHECKED_CAST")
		return if (isMemoryCache) cacheMap else ObjectInputStream(FileInputStream(cacheFile)).readObject() as LinkedHashMap<URI, HttpCache>
	}
}
