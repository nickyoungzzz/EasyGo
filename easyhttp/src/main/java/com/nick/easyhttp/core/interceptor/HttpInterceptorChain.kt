package com.nick.easyhttp.core.interceptor

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

internal class HttpInterceptorChain(private val httpInterceptors: List<HttpInterceptor>, private var index: Int = 0, private val httpReq: HttpReq) : HttpInterceptor.Chain {

    override fun proceed(httpReq: HttpReq): HttpResp {
        if (index >= httpInterceptors.size) {
            throw AssertionError()
        }
        val next = HttpInterceptorChain(httpInterceptors, index + 1, httpReq)
        val interceptor = httpInterceptors[index]
        return interceptor.intercept(next)
    }

    override fun request(): HttpReq {
        return httpReq
    }
}