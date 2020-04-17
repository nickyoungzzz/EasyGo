package com.nick.easyhttp.config

import com.nick.easyhttp.core.download.IDownloadHandler
import com.nick.easyhttp.core.download.OkIoDownHandler
import com.nick.easyhttp.core.req.IHttpHandler
import com.nick.easyhttp.core.req.okhttp.OkHttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import com.nick.easyhttp.util.SslHelper
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

	constructor() : this(Builder())

	fun newBuilder() = Builder(this)

	class Builder constructor() {
		internal var proxy: Proxy = Proxy.NO_PROXY
		internal var httpHandler: IHttpHandler = OkHttpHandler()
		internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
		internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
		internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
		internal var downloadHandler: IDownloadHandler = OkIoDownHandler()
		internal var connectTimeOut: Long = 15000L
		internal var readTimeOut: Long = 15000L
		internal var writeTimeOut: Long = 15000L
		internal var interceptor = fun(_: HttpReq, httpResp: HttpResp) = httpResp

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

		fun build() = HttpConfig(this)
	}
}