package com.nick.easyhttp.core.interceptor

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

fun interface HttpInterceptor {
    fun intercept(chain: Chain): HttpResp

    interface Chain {
        fun proceed(httpReq: HttpReq): HttpResp
        fun request(): HttpReq
    }
}