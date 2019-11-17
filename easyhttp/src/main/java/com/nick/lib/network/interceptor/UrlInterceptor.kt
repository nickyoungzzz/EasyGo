package com.nick.lib.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class UrlInterceptor(var url: String) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val original = chain.request()
		return chain.proceed(original.newBuilder().url(url).build())
	}
}