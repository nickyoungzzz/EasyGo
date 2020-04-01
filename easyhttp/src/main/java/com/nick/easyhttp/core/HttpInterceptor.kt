package com.nick.easyhttp.core

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

interface HttpInterceptor {
	fun beforeExecute(httpReq: HttpReq): HttpReq
	fun afterExecute(httpReq: HttpReq, httpResp: HttpResp): HttpResp
}