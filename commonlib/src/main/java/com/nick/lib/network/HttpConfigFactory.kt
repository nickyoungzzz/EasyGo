package com.nick.lib.network

import com.nick.lib.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

internal object HttpConfigFactory {

	private const val TIMEOUT = 30L

	internal var okHttpClientBuilder = OkHttpClient.Builder()
		.sslSocketFactory(SslHelper.getSSLSocketFactory(), SslHelper.getTrustManager())
		.hostnameVerifier(SslHelper.getHostnameVerifier())
		.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
		.readTimeout(TIMEOUT, TimeUnit.SECONDS)
		.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
		.callTimeout(TIMEOUT, TimeUnit.SECONDS)
		.proxy(Proxy.NO_PROXY)
		.addInterceptor(run {
			val httpLoggingInterceptor = HttpLoggingInterceptor()
			httpLoggingInterceptor.apply {
				this.level = if (BuildConfig.DEBUG)
					HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
			}
		})

	internal var retrofitBuilder = Retrofit.Builder()
		.baseUrl("https://www.baidu.com/")
		.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
		.addConverterFactory(ScalarsConverterFactory.create())
		.client(okHttpClientBuilder.build())

	internal var retrofit: Retrofit? = null
}
