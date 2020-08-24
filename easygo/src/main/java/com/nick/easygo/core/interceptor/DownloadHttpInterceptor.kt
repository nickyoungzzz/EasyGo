package com.nick.easygo.core.interceptor

import com.nick.easygo.result.HttpResp

internal class DownloadHttpInterceptor(private val breakPoint: Boolean, private val range: Long) : HttpInterceptor {

	override fun intercept(chain: HttpInterceptor.Chain): HttpResp {
		val originalHttpReq = chain.request()
		if (breakPoint && range > 0) {
			return chain.proceed(originalHttpReq.newBuilder().addHeader("Range", "bytes=${range}-").build())
		}
		return chain.proceed(originalHttpReq)
	}
}