package com.nick.easyhttp.core.req

import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

interface IHttpHandler {
	fun execute(httpReq: HttpReq): HttpResp
	fun cancel()
}