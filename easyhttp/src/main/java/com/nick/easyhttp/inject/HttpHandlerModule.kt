package com.nick.easyhttp.inject

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.config.HttpConfig
import com.nick.easyhttp.core.cookie.HttpHandlerCookie
import com.nick.easyhttp.core.req.urlconnection.UrlConnectionClient
import dagger.Module
import dagger.Provides
import okhttp3.*
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
class HttpHandlerModule constructor(@set:Inject @get:Inject var config: HttpConfig) {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().proxy(config.proxy)
                .readTimeout(config.readTimeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
                .hostnameVerifier(config.hostnameVerifier)
                .sslSocketFactory(config.sslSocketFactory, config.x509TrustManager)
                .dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        return config.dns(hostname).toList()
                    }
                })
                .cookieJar(object : CookieJar {
                    val cookieHandler = config.httpCookieHandler
                    override fun loadForRequest(url: HttpUrl): List<Cookie> {
                        return if (!cookieHandler.useCookieForRequest(url.toUri())) emptyList() else run {
                            val cookieList = EasyHttp.cookieMap[url.toUri()] ?: emptyList()
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
                            EasyHttp.cookieMap[uri] = httpHandlerCookies
                        }
                    }
                })
                .build()
    }

    @Singleton
    @Provides
    fun provideUrlConnectionClient(): UrlConnectionClient {
        return UrlConnectionClient.Builder().proxy(config.proxy)
                .readTimeOut(config.readTimeOut)
                .connectTimeOut(config.connectTimeout)
                .hostNameVerifier(config.hostnameVerifier)
                .sslSocketFactory(config.sslSocketFactory)
                .x509TrustManager(config.x509TrustManager)
                .interceptor(config.interceptor)
                .dns(config.dns)
                .build()
    }
}