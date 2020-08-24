package com.nick.easygo.core.req

import com.nick.easygo.core.req.okhttp.OkHttpHandler
import com.nick.easygo.core.req.urlconnection.UrlConnectionHandler
import com.nick.easygo.result.HttpReq
import com.nick.easygo.result.HttpResp

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