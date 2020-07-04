package com.nick.easyhttp.core

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.config.HttpConfig
import com.nick.easyhttp.core.download.DownParam
import com.nick.easyhttp.core.download.DownState
import com.nick.easyhttp.core.download.DownloadHandler
import com.nick.easyhttp.core.param.HttpParam
import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.result.*
import java.io.IOException

class HttpEmitter internal constructor(var param: HttpParam) {

	private var reqTag: Any? = null

	private var asDownload = false

	private var httpConfig: HttpConfig = EasyHttp.httpConfig

	private var httpHandler: HttpHandler = httpConfig.httpHandler

	private var downloadHandler: DownloadHandler = httpConfig.downLoadHandler

	private lateinit var downParam: DownParam

	fun tag(reqTag: Any?) {
		this.reqTag = reqTag
	}

	fun asDownload(down: DownParam.() -> Unit) {
		this.asDownload = true
		this.downParam = DownParam().apply(down)
	}

	fun httpHandler(httpHandler: HttpHandler) {
		this.httpHandler = httpHandler
	}

	private var beforeExecute = fun(httpReq: HttpReq): HttpReq = httpReq

	private var afterExecute = fun(_: HttpReq, httpResp: HttpResp) = httpResp

	fun whenLaunch(before: (httpReq: HttpReq) -> HttpReq) {
		this.beforeExecute = before
	}

	fun afterLaunch(after: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp) {
		this.afterExecute = after
	}

	fun downloadHandler(downloadHandler: DownloadHandler) {
		this.downloadHandler = downloadHandler
	}

	private fun httpReq(): HttpReq {
		return HttpReq(param.url, param.reqMethod, this@HttpEmitter.reqTag, HttpReqHead(param.headerMap, param.queryMap),
			HttpReqBody(param.fieldMap, param.multipartBody, param.isMultiPart, param.jsonString), this@HttpEmitter.asDownload)
	}

	fun deploy(extra: HttpEmitter.() -> Unit) = apply(extra)

	fun launch(init: HttpResult.() -> Unit = {}): HttpResult {
		val httpReq = beforeExecute(httpConfig.before(httpReq()))
		val httpResp = afterExecute(httpReq, httpConfig.after(httpReq, httpHandler.execute(httpReq)))
		val status = if (httpResp.exception != null) HttpStatus.EXCEPTION
		else (if (httpResp.isSuccessful) HttpStatus.SUCCESS else HttpStatus.ERROR)
		return HttpResult(httpResp.code, httpResp.headers, httpResp.resp, httpResp.exception, status).apply(init)
	}

	fun download(exc: (e: Throwable) -> Unit = {}, download: (downState: DownState) -> Unit = {}) {
		val source = downParam.desSource
		val range = if (downParam.breakPoint && source.exists()) source.length() else 0
		val httpReqHead = httpReq().httpReqHead
		val httpResp = httpHandler.execute(httpReq().newBuilder().httpReqHead(httpReqHead.newBuilder().addHeader("Range", "bytes=${range}-").build()).build())
		if (httpResp.isSuccessful) {
			try {
				downloadHandler.saveFile(httpResp.inputStream!!, downParam, httpResp.contentLength) { state ->
					download(state)
				}
			} catch (e: IOException) {
				exc(e)
			}
		} else {
			exc(httpResp.exception!!)
		}
	}

	fun cancelRequest() {
		httpHandler.cancel()
		if (asDownload) {
			downloadHandler.cancel()
		}
	}
}