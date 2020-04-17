package com.nick.easyhttp.config

import com.nick.easyhttp.core.req.urlconnection.UrlConnectionClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object EasyHttp {

	var okHttpClient: OkHttpClient = OkHttpClient()
		private set

	var urlConnectionClient: UrlConnectionClient = UrlConnectionClient()
		private set

	var httpConfig: HttpConfig = HttpConfig()
		private set

	@Volatile private var hasConfig = false

	@JvmStatic
	@Synchronized fun init(config: HttpConfig) {

		if (hasConfig) throw RuntimeException("Do not config again!!!")

		okHttpClient = okHttpClient.newBuilder().proxy(config.proxy)
			.readTimeout(config.readTimeOut, TimeUnit.MILLISECONDS)
			.connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
			.hostnameVerifier(config.hostnameVerifier)
			.sslSocketFactory(config.sslSocketFactory, config.x509TrustManager)
			.build()

		urlConnectionClient = urlConnectionClient.newBuilder().proxy(config.proxy)
			.readTimeOut(config.readTimeOut)
			.connectTimeOut(config.connectTimeout)
			.hostNameVerifier(config.hostnameVerifier)
			.sslSocketFactory(config.sslSocketFactory)
			.x509TrustManager(config.x509TrustManager)
			.downloadHandler(config.downLoadHandler)
			.interceptor(config.interceptor)
			.build()

		hasConfig = true
	}
}