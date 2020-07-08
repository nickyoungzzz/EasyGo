package com.nick.easyhttp.core.interceptor

import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.result.HttpResp

internal class LaunchHttpInterceptor(private val httpHandler: HttpHandler) : HttpInterceptor {

	override fun intercept(chain: HttpInterceptor.Chain): HttpResp {
		return httpHandler.execute(chain.request())
	}
}