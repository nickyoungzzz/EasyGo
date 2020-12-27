package com.nick.easygo.core.res

import com.nick.easygo.core.interceptor.HttpInterceptorChain
import com.nick.easygo.result.HttpReq
import com.nick.easygo.result.HttpResp

class RealHttpEmitter(private val httpInterceptorChain: HttpInterceptorChain, private val httpReq: HttpReq) {
	fun emit(): HttpResp {
		return httpInterceptorChain.proceed(httpReq)
	}
}

