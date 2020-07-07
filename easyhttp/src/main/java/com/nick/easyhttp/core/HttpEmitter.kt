package com.nick.easyhttp.core

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.config.HttpConfig
import com.nick.easyhttp.core.download.DownParam
import com.nick.easyhttp.core.download.DownState
import com.nick.easyhttp.core.download.DownloadHandler
import com.nick.easyhttp.core.param.HttpParam
import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.core.req.HttpReqInterceptor
import com.nick.easyhttp.core.req.HttpRespInterceptor
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpReqBody
import com.nick.easyhttp.result.HttpReqHead
import com.nick.easyhttp.result.HttpResult
import java.io.IOException

class HttpEmitter internal constructor(private val param: HttpParam) {

	private var reqTag: Any? = null

	private var asDownload = false

	private var httpConfig: HttpConfig = EasyHttp.httpConfig

	private var httpHandler: HttpHandler = httpConfig.httpHandler

	private var downloadHandler: DownloadHandler = httpConfig.downLoadHandler

	private lateinit var downParam: DownParam

	private val httpReqInterceptors = ArrayList<HttpReqInterceptor>()

	private val httpRespInterceptors = ArrayList<HttpRespInterceptor>()

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

	fun whenLaunch(httpReqInterceptor: HttpReqInterceptor) {
		this.httpReqInterceptors.add(httpReqInterceptor)
	}

	fun afterLaunch(httpRespInterceptor: HttpRespInterceptor) {
		this.httpRespInterceptors.add(httpRespInterceptor)
	}

	fun downloadHandler(downloadHandler: DownloadHandler) {
		this.downloadHandler = downloadHandler
	}

	private fun httpReq(): HttpReq {
		return HttpReq(param.url, param.reqMethod, this@HttpEmitter.reqTag, HttpReqHead(param.headerMap, param.queryMap),
			HttpReqBody(param.fieldMap, param.multipartBody, param.isMultiPart, param.jsonString), this@HttpEmitter.asDownload)
			.let {
				it.newBuilder().httpReqHead(it.httpReqHead.newBuilder().addHeader("request-client", httpHandler.requestClient).build()).build()
			}
	}

	fun deploy(extra: HttpEmitter.() -> Unit) = apply(extra)

	fun launch(init: HttpResult.() -> Unit = {}): HttpResult {
		var httpReq = httpReq()
		httpReqInterceptors.apply { addAll(0, httpConfig.httpReqInterceptors) }.forEach { httpReqInterceptor ->
			httpReq = httpReqInterceptor.intercept(httpReq)
		}
		var httpResp = httpHandler.execute(httpReq)
		httpRespInterceptors.apply { addAll(0, httpConfig.httpRespInterceptors) }.forEach { httpRespInterceptor ->
			httpResp = httpRespInterceptor.intercept(httpReq, httpResp)
		}
		val status = if (httpResp.exception != null) HttpStatus.EXCEPTION
		else (if (httpResp.isSuccessful) HttpStatus.SUCCESS else HttpStatus.ERROR)
		return HttpResult(httpResp.code, httpResp.headers, httpResp.resp, httpResp.exception, status).apply(init)
	}

	fun download(exc: (e: Throwable) -> Unit = {}, download: (downState: DownState) -> Unit = {}) {
		val source = downParam.desSource
		val range = if (downParam.breakPoint && source.exists()) source.length() else 0
		val httpResp = httpHandler.execute(httpReq().newBuilder().httpReqHead(httpReq().httpReqHead.newBuilder().addHeader("Range", "bytes=${range}-").build()).build())
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

	fun cancel() {
		httpHandler.cancel()
		if (asDownload) {
			downloadHandler.cancel()
		}
	}
}