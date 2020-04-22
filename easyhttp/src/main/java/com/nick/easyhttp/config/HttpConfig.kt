package com.nick.easyhttp.config

import com.nick.easyhttp.core.cookie.IHttpCookieHandler
import com.nick.easyhttp.core.download.IDownloadHandler
import com.nick.easyhttp.core.req.IHttpHandler
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
	var interceptor = builder.interceptor
	var dns = builder.dns
	var httpCookieHandler = builder.httpCookieHandler

	constructor() : this(Builder())

	fun newBuilder() = Builder(this)

	companion object {
		private const val TIMEOUT = 15000L
	}

	class Builder constructor() {
		internal var proxy: Proxy = Proxy.NO_PROXY
		internal var httpHandler: IHttpHandler = IHttpHandler.OK_HTTP_HANDLER
		internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
		internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
		internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
		internal var downloadHandler: IDownloadHandler = IDownloadHandler.OKIO_DOWNLOADHANDLER
		internal var connectTimeOut: Long = TIMEOUT
		internal var readTimeOut: Long = TIMEOUT
		internal var writeTimeOut: Long = TIMEOUT
		internal var interceptor = fun(_: HttpReq, httpResp: HttpResp) = httpResp
		internal var dns = fun(host: String): Array<InetAddress> = InetAddress.getAllByName(host)
		internal var httpCookieHandler = IHttpCookieHandler.NO_COOKIE

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
			this.interceptor = httpConfig.interceptor
			this.dns = httpConfig.dns
		}

		fun proxy(proxy: Proxy) = apply { this.proxy = proxy }

		fun httpHandler(httpHandler: IHttpHandler) = apply { this.httpHandler = httpHandler }

		fun hostNameVerifier(hostNameVerifier: HostnameVerifier) = apply { this.hostNameVerifier = hostNameVerifier }

		fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) = apply { this.sslSocketFactory = sslSocketFactory }

		fun x509TrustManager(x509TrustManager: X509TrustManager) = apply { this.x509TrustManager = x509TrustManager }

		fun downloadHandler(downloadHandler: IDownloadHandler) = apply { this.downloadHandler = downloadHandler }

		fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

		fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

		fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

		fun interceptor(interceptor: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp) = apply { this.interceptor = interceptor }

		fun dns(dns: (host: String) -> Array<InetAddress>) = apply { this.dns = dns }

		fun httpCookieHandler(httpCookieHandler: IHttpCookieHandler) = apply { this.httpCookieHandler = httpCookieHandler }

		fun build() = HttpConfig(this)
	}
}