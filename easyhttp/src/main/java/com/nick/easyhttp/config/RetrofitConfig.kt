package com.nick.easyhttp.config

import com.nick.easyhttp.util.SslHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

class RetrofitConfig : IHttpHandlerConfig {

	override fun config() {
		retrofit = retrofitBuilder.baseUrl("https://www.baidu.com").client(okHttpClientBuilder.build()).build()
	}

	override fun needConfig(): Boolean {
		return retrofit == null
	}

	companion object {
		private const val TIMEOUT = 15L
		private var okHttpClientBuilder = OkHttpClient.Builder()
			.sslSocketFactory(SslHelper.getSSLSocketFactory(), SslHelper.getTrustManager())
			.hostnameVerifier(SslHelper.getHostnameVerifier())
			.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
			.readTimeout(TIMEOUT, TimeUnit.SECONDS)
			.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
			.proxy(Proxy.NO_PROXY)

		internal var retrofitBuilder = Retrofit.Builder()
			.baseUrl("https://www.baidu.com/")
			.addConverterFactory(ScalarsConverterFactory.create())
			.client(okHttpClientBuilder.build())

		var retrofit: Retrofit? = null
	}
}