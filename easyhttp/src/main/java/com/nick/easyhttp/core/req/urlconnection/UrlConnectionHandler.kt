package com.nick.easyhttp.core.req.urlconnection

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.core.req.IHttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp

open class UrlConnectionHandler : IHttpHandler {

	private val urlConnectionClient: UrlConnectionClient = EasyHttp.urlConnectionClient

	override fun execute(httpReq: HttpReq): HttpResp {

		val urlConnectionReq = UrlConnectionReq.Builder()
			.reqMethod(httpReq.reqMethod).reqTag(httpReq.reqTag).url(httpReq.url)
			.asDownload(httpReq.asDownload).isMultiPart(httpReq.isMultiPart)
			.jsonString(httpReq.jsonString).fieldMap(httpReq.fieldMap)
			.headerMap(httpReq.headerMap).queryMap(httpReq.queryMap)
			.multipartBody(httpReq.multipartBody).build()

		val urlConnectionResp = urlConnectionClient.execute(urlConnectionReq)
		return HttpResp.Builder().code(urlConnectionResp.code).contentLength(urlConnectionResp.contentLength)
			.isSuccessful(urlConnectionResp.isSuccessful).exception(urlConnectionResp.exception)
			.byteData(urlConnectionResp.inputStream).headers(urlConnectionResp.headers)
			.resp(urlConnectionResp.resp).build()
	}

	override fun cancel() {
		urlConnectionClient.cancel()
	}
}