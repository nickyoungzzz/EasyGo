package com.nick.easyhttp.core.req

import com.nick.easyhttp.core.req.okhttp.OkHttpHandler
import com.nick.easyhttp.core.req.urlconnection.UrlConnectionHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

interface HttpHandler {
	fun execute(httpReq: HttpReq): HttpResp
	fun cancel()
	val requestClient: String

	companion object {
		@JvmField
		val OK_HTTP_HANDLER: HttpHandler = OkHttpHandler()

		@JvmField
		val URL_CONNECTION_HANDLER: HttpHandler = UrlConnectionHandler()
	}
}