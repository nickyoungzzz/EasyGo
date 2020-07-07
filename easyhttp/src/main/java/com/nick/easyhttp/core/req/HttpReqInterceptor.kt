package com.nick.easyhttp.core.req

import com.nick.easyhttp.result.HttpReq

fun interface HttpReqInterceptor {
    fun intercept(httpReq: HttpReq): HttpReq
}