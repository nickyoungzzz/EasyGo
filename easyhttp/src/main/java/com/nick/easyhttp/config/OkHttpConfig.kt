package com.nick.easyhttp.config

import com.nick.easyhttp.util.SslHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.Proxy
import java.util.concurrent.TimeUnit

open class OkHttpConfig : IHttpHandlerConfig {

	override fun config() {
		okHttpClient = okHttpClientBuilder.build()
	}

	override fun needConfig(): Boolean {
		return okHttpClient == null
	}

	companion object {
		private const val TIMEOUT = 15L
		var okHttpClientBuilder = OkHttpClient.Builder()
			.sslSocketFactory(SslHelper.getSSLSocketFactory(), SslHelper.getTrustManager())
			.hostnameVerifier(SslHelper.getHostnameVerifier())
			.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
			.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
			.readTimeout(TIMEOUT, TimeUnit.SECONDS)
			.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
			.proxy(Proxy.NO_PROXY)
		var okHttpClient: OkHttpClient? = null
	}
}