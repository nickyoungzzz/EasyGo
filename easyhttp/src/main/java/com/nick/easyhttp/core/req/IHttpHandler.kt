package com.nick.easyhttp.core.req

import com.nick.easyhttp.core.req.okhttp.OkHttpHandler
import com.nick.easyhttp.core.req.urlconnection.UrlConnectionHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

interface IHttpHandler {
	fun execute(httpReq: HttpReq): HttpResp
	fun cancel()

	companion object {
		@JvmField
		val OK_HTTP_HANDLER: IHttpHandler = OkHttpHandler()

		@JvmField
		val URL_CONNECTION_HANDLER: IHttpHandler = UrlConnectionHandler()
	}
}