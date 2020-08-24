package com.nick.easygo.core.interceptor

import com.nick.easygo.result.HttpReq
import com.nick.easygo.result.HttpResp

fun interface HttpInterceptor {
	fun intercept(chain: Chain): HttpResp

	interface Chain {
		fun proceed(httpReq: HttpReq): HttpResp
		fun request(): HttpReq
	}
}