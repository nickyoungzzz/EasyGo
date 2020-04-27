package com.nick.easyhttp.config

import com.nick.easyhttp.core.cache.HttpCacheHandler
import com.nick.easyhttp.core.cookie.HttpCookieHandler
import com.nick.easyhttp.core.download.DownloadHandler
import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import com.nick.easyhttp.util.SslHelper
import java.net.InetAddress
import java.net.Proxy
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class HttpConfig internal constructor(builder: Builder) {

    var proxy = builder.proxy
    var httpHandler = builder.httpHandler
    var hostnameVerifier = builder.hostNameVerifier
    var sslSocketFactory = builder.sslSocketFactory
    var x509TrustManager = builder.x509TrustManager
    var downLoadHandler = builder.downloadHandler
    var connectTimeout = builder.connectTimeOut
    var readTimeOut = builder.readTimeOut
    var writeTimeOut = builder.writeTimeOut
    var before = builder.before
    var after = builder.after
    var dns = builder.dns
    var httpCookieHandler = builder.httpCookieHandler
    var httpCacheHandler = builder.httpCacheHandler
    var timeoutConfig = builder.timeoutConfig

    constructor() : this(Builder())

    fun newBuilder() = Builder(this)

    companion object {
        private const val TIMEOUT = 15000L
        val DEFAULT_CONFIG = HttpConfig()
    }

    class Builder constructor() {
        internal var proxy: Proxy = Proxy.NO_PROXY
        internal var httpHandler: HttpHandler = HttpHandler.OK_HTTP_HANDLER
        internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
        internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
        internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
        internal var downloadHandler: DownloadHandler = DownloadHandler.OKIO_DOWNLOAD_HANDLER
        internal var connectTimeOut: Long = TIMEOUT
        internal var readTimeOut: Long = TIMEOUT
        internal var writeTimeOut: Long = TIMEOUT
        internal var before = fun(httpReq: HttpReq) = beforeReq(httpReq)
        internal var after = fun(httpReq: HttpReq, httpResp: HttpResp) = afterReq(httpReq, httpResp)
        internal var dns = fun(host: String): Array<InetAddress> = InetAddress.getAllByName(host)
        internal var httpCookieHandler = HttpCookieHandler.NO_COOKIE
        internal var httpCacheHandler = HttpCacheHandler.MEMORY_CACHE
        internal var timeoutConfig = TimeoutConfig.DEFAULT_TIMEOUT

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
            this.before = httpConfig.before
            this.after = httpConfig.after
            this.dns = httpConfig.dns
            this.httpCookieHandler = httpConfig.httpCookieHandler
            this.httpCacheHandler = httpConfig.httpCacheHandler
            this.timeoutConfig = httpConfig.timeoutConfig
        }

        private var beforeReq = fun(httpReq: HttpReq) = httpReq

        private var afterReq = fun(_: HttpReq, httpResp: HttpResp) = httpResp

        fun proxy(proxy: Proxy) = apply { this.proxy = proxy }

        fun httpHandler(httpHandler: HttpHandler) = apply { this.httpHandler = httpHandler }

        fun hostNameVerifier(hostNameVerifier: HostnameVerifier) = apply { this.hostNameVerifier = hostNameVerifier }

        fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) = apply { this.sslSocketFactory = sslSocketFactory }

        fun x509TrustManager(x509TrustManager: X509TrustManager) = apply { this.x509TrustManager = x509TrustManager }

        fun downloadHandler(downloadHandler: DownloadHandler) = apply { this.downloadHandler = downloadHandler }

        fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

        fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

        fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

        fun intercept(before: (httpReq: HttpReq) -> HttpReq = beforeReq, after: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp = afterReq) = apply {
            this.before = before
            this.after = after
        }

        fun dns(dns: (host: String) -> Array<InetAddress>) = apply { this.dns = dns }

        fun httpCookieHandler(httpCookieHandler: HttpCookieHandler) = apply { this.httpCookieHandler = httpCookieHandler }

        fun httpCacheHandler(httpCacheHandler: HttpCacheHandler) = apply { this.httpCacheHandler = httpCacheHandler }

        fun timeoutHandler(timeoutConfig: TimeoutConfig) = apply { this.timeoutConfig = timeoutConfig }

        fun build() = HttpConfig(this)
    }
}