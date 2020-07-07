package com.nick.easyhttp.core.req

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

fun interface HttpRespInterceptor {
    fun intercept(httpReq: HttpReq, httpResp: HttpResp): HttpResp
}