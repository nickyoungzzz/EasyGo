package com.nick.easyhttp.config

import com.nick.easyhttp.core.cookie.HttpHandlerCookie
import java.net.*

object EasyHttp {

    var httpConfig: HttpConfig = HttpConfig.DEFAULT_CONFIG
        private set

    lateinit var cookieMap: LinkedHashMap<URI, List<HttpHandlerCookie>>

    @Volatile
    private var hasConfig = false

    @JvmStatic
    @Synchronized
    fun init(config: HttpConfig) {
        if (hasConfig) throw RuntimeException("Do not config again!!!")
        this.httpConfig = config
        cookieMap = object : LinkedHashMap<URI, List<HttpHandlerCookie>>() {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<URI, List<HttpHandlerCookie>>?): Boolean {
                return size >= httpConfig.httpCookieHandler.maxCookieCount()
            }
        }
        CookieHandler.setDefault(CookieManager(object : CookieStore {

            override fun removeAll(): Boolean {
                cookieMap.clear()
                return cookieMap.size == 0
            }

            override fun add(uri: URI, cookie: HttpCookie) {
                val httpHandlerCookie = httpCookie2HttpHandlerCookie(cookie)
                synchronized(EasyHttp::class) {
                    if (cookieMap.containsKey(uri)) {
                        val cookieList = cookieMap[uri]?.toMutableList()
                        if (cookieList?.size ?: 0 >= httpConfig.httpCookieHandler.uriCookieCount(uri)) {
                            cookieList?.removeAt(0)
                        }
                        cookieList?.add(httpHandlerCookie)
                    } else {
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
