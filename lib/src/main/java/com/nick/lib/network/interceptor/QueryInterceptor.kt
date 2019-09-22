package com.nick.lib.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class QueryInterceptor(var queryMap: HashMap<String, String>) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val original = chain.request()
		val urlBuilder = original.url().newBuilder()
		if (queryMap.isNotEmpty()) {
			queryMap.forEach {
				urlBuilder.addQueryParameter(it.key, it.value)
			}
		}
		return chain.proceed(original.newBuilder().url(urlBuilder.build()).build())
	}
}