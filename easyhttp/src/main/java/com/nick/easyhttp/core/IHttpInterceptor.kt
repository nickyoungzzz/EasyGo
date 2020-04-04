package com.nick.easyhttp.core

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

interface IHttpInterceptor {
	fun beforeExecute(httpReq: HttpReq): HttpReq = httpReq
	fun afterExecute(httpReq: HttpReq, httpResp: HttpResp): HttpResp = httpResp
}