package com.nick.easygo.core.interceptor

import com.nick.easygo.core.req.HttpHandler
import com.nick.easygo.result.HttpResp

internal class LaunchHttpInterceptor(private val httpHandler: HttpHandler) : HttpInterceptor {

	override fun intercept(chain: HttpInterceptor.Chain): HttpResp {
		return httpHandler.execute(chain.request())
	}
}