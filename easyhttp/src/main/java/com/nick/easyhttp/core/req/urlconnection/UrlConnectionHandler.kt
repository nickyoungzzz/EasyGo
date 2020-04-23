package com.nick.easyhttp.core.req.urlconnection

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.core.req.IHttpHandler
import com.nick.easyhttp.inject.DaggerHttpHandlerComponent
import com.nick.easyhttp.inject.HttpHandlerModule
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import javax.inject.Inject

class UrlConnectionHandler : IHttpHandler {

	@Inject
	lateinit var urlConnectionClient: UrlConnectionClient

	override fun execute(httpReq: HttpReq): HttpResp {
		DaggerHttpHandlerComponent.builder().httpHandlerModule(HttpHandlerModule(EasyHttp.httpConfig)).build().inject(this)
		val urlConnectionReq = UrlConnectionReq.Builder()
			.reqMethod(httpReq.reqMethod).reqTag(httpReq.reqTag).url(httpReq.url)
			.asDownload(httpReq.asDownload).isMultiPart(httpReq.isMultiPart)
			.jsonString(httpReq.jsonString).fieldMap(httpReq.fieldMap)
			.headerMap(httpReq.headerMap).queryMap(httpReq.queryMap)
			.multipartBody(httpReq.multipartBody).build()

		val urlConnectionResp = urlConnectionClient.proceed(urlConnectionReq)
		return HttpResp.Builder().code(urlConnectionResp.code).contentLength(urlConnectionResp.contentLength)
			.isSuccessful(urlConnectionResp.isSuccessful).exception(urlConnectionResp.exception)
			.byteData(urlConnectionResp.inputStream).headers(urlConnectionResp.headers)
			.resp(urlConnectionResp.resp).build()
	}

	override fun cancel() {
		urlConnectionClient.cancel()
	}
}