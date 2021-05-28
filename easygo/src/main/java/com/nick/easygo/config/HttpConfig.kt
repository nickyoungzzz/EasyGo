package com.nick.easygo.config

import com.nick.easygo.converter.GSONDataConverter
import com.nick.easygo.converter.ResDataConverter
import com.nick.easygo.core.cache.HttpCacheHandler
import com.nick.easygo.core.cookie.HttpCookieHandler
import com.nick.easygo.core.download.DownloadHandler
import com.nick.easygo.core.interceptor.HttpInterceptor
import com.nick.easygo.core.req.HttpHandler
import com.nick.easygo.util.SslHelper
import java.net.InetAddress
import java.net.Proxy
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class HttpConfig internal constructor(builder: Builder) {

    val proxy = builder.proxy
    val httpHandler = builder.httpHandler
    val hostnameVerifier = builder.hostNameVerifier
    val sslSocketFactory = builder.sslSocketFactory
    val x509TrustManager = builder.x509TrustManager
    val downLoadHandler = builder.downloadHandler
    val connectTimeout = builder.connectTimeOut
    val readTimeOut = builder.readTimeOut
    val writeTimeOut = builder.writeTimeOut
    val dns = builder.dns
    val httpCookieHandler = builder.httpCookieHandler
    val httpCacheHandler = builder.httpCacheHandler
    val timeoutHandler = builder.timeoutHandler
    val httpInterceptors = builder.httpInterceptors
    val resDataConverter = builder.resDataConverter

    constructor() : this(Builder())

    fun newBuilder() = Builder(this)

    class Builder constructor() {
        internal var proxy: Proxy = Proxy.NO_PROXY
            private set
        internal var httpHandler: HttpHandler = HttpHandler.OK_HTTP_HANDLER
            private set
        internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
            private set
        internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
            private set
        internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
            private set
        internal var downloadHandler: DownloadHandler = DownloadHandler.OK_IO_DOWNLOAD_HANDLER
            private set
        internal var connectTimeOut: Long = 15000L
            private set
        internal var readTimeOut: Long = 15000L
            private set
        internal var writeTimeOut: Long = 15000L
            private set
        internal var dns = fun(host: String): Array<InetAddress> = InetAddress.getAllByName(host)
            private set
        internal var httpCookieHandler = HttpCookieHandler.NO_COOKIE
            private set
        internal var httpCacheHandler = HttpCacheHandler.MEMORY_CACHE
            private set
        internal var timeoutHandler =
            fun(_: String, _: Any?, _: String, _: Map<String, List<String>>): TimeoutConfig? = null
            private set
        internal val httpInterceptors = ArrayList<HttpInterceptor>()
        internal var resDataConverter: ResDataConverter = GSONDataConverter.create()
            private set

        constructor(httpConfig: HttpConfig) : this() {
            this.proxy = httpConfig.proxy
            this.httpHandler = httpConfig.httpHandler
            this.hostNameVerifier = httpConfig.hostnameVerifier
            this.sslSocketFactory = httpConfig.sslSocketFactory
            this.x509TrustManager = httpConfig.x509TrustManager
            this.downloadHandler = httpConfig.downLoadHandler
            this.connectTimeOut = httpConfig.connectTimeout
            this.readTimeOut = httpConfig.readTimeOut
            this.writeTimeOut = httpConfig.writeTimeOut
            this.dns = httpConfig.dns
            this.httpCookieHandler = httpConfig.httpCookieHandler
            this.httpCacheHandler = httpConfig.httpCacheHandler
            this.httpInterceptors.addAll(httpConfig.httpInterceptors)
            this.resDataConverter = httpConfig.resDataConverter
        }

        fun proxy(proxy: Proxy) = apply { this.proxy = proxy }

        fun httpHandler(httpHandler: HttpHandler) = apply { this.httpHandler = httpHandler }

        fun hostNameVerifier(hostNameVerifier: HostnameVerifier) =
            apply { this.hostNameVerifier = hostNameVerifier }

        fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) =
            apply { this.sslSocketFactory = sslSocketFactory }

        fun x509TrustManager(x509TrustManager: X509TrustManager) =
            apply { this.x509TrustManager = x509TrustManager }

        fun downloadHandler(downloadHandler: DownloadHandler) =
            apply { this.downloadHandler = downloadHandler }

        fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

        fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

        fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

        fun addInterceptor(httpInterceptor: HttpInterceptor) =
            apply { this.httpInterceptors.add(httpInterceptor) }

        fun dns(dns: (host: String) -> Array<InetAddress>) = apply { this.dns = dns }

        fun httpCookieHandler(httpCookieHandler: HttpCookieHandler) =
            apply { this.httpCookieHandler = httpCookieHandler }

        fun httpCacheHandler(httpCacheHandler: HttpCacheHandler) =
            apply { this.httpCacheHandler = httpCacheHandler }

        fun timeoutHandler(timeoutHandler: (url: String, tag: Any?, method: String, headers: Map<String, List<String>>) -> TimeoutConfig) =
            apply {
                this.timeoutHandler = timeoutHandler
            }

        fun resDataConverter(resDataConverter: ResDataConverter) = apply {
            this.resDataConverter = resDataConverter
        }

        fun build() = HttpConfig(this)
    }
}