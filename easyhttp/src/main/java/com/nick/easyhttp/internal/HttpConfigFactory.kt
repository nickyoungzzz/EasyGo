package com.nick.easyhttp.internal

import com.nick.easyhttp.util.SslHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

internal object HttpConfigFactory {

	private const val TIMEOUT = 15L

	internal var okHttpClientBuilder = OkHttpClient.Builder()
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

	internal var retrofit: Retrofit? = null
}
