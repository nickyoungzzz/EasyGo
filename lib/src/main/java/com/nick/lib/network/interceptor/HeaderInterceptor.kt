package com.nick.lib.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(var headerMap: HashMap<String, String>) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val original = chain.request()
		val requestBuilder = original.newBuilder()
		if (headerMap.isNotEmpty()) {
			headerMap.forEach {
				if (original.header(it.key) != null) {
					requestBuilder.addHeader(it.key, it.value)
				}
			}
		}
		return chain.proceed(requestBuilder.build())
	}
}