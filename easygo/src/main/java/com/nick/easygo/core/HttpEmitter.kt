package com.nick.easygo.core

import com.nick.easygo.config.EasyGo
import com.nick.easygo.config.HttpConfig
import com.nick.easygo.core.download.DownParam
import com.nick.easygo.core.download.DownloadHandler
import com.nick.easygo.core.interceptor.DownloadHttpInterceptor
import com.nick.easygo.core.interceptor.HttpInterceptor
import com.nick.easygo.core.interceptor.HttpInterceptorChain
import com.nick.easygo.core.interceptor.LaunchHttpInterceptor
import com.nick.easygo.core.param.HttpParam
import com.nick.easygo.core.req.HttpHandler
import com.nick.easygo.core.res.RealHttpEmitter
import com.nick.easygo.result.*
import com.nick.easygo.util.reflect.TypeTaken

class HttpEmitter internal constructor(private val param: HttpParam) {

	@PublishedApi
	internal var httpConfig: HttpConfig = EasyGo.httpConfig

	@PublishedApi
	internal var downloadHandler: DownloadHandler = httpConfig.downLoadHandler

	@PublishedApi
	internal var downParam: DownParam = DownParam()
	private var httpHandler: HttpHandler = httpConfig.httpHandler
	private var reqTag: Any? = null
	private var asDownload = false
	private val httpInterceptors = ArrayList<HttpInterceptor>()

	fun tag(reqTag: Any?) = apply {
		this.reqTag = reqTag
	}

	fun configDownload(down: DownParam.() -> Unit) = apply {
		this.asDownload = true
		this.downParam = DownParam().apply(down)
	}

	fun httpHandler(httpHandler: HttpHandler) = apply {
		this.httpHandler = httpHandler
	}

	fun addInterceptor(httpInterceptor: HttpInterceptor) = apply {
		httpInterceptors.add(httpInterceptor)
	}

	fun downloadHandler(downloadHandler: DownloadHandler) = apply {
		this.downloadHandler = downloadHandler
	}

	@PublishedApi
	internal fun generateHttpReq(): HttpReq {
		return HttpReq(
			param.url, param.reqMethod, this@HttpEmitter.reqTag, param.headerMap, param.queryMap,
			HttpReqBody(param.fieldMap, param.multipartBody, param.isMultiPart, param.jsonString), this@HttpEmitter.asDownload
		).newBuilder().addHeader("request-client", httpHandler.requestClient).build()
	}

	@PublishedApi
	internal fun generateRealHttpEmitter(): RealHttpEmitter {
		val httpReq = generateHttpReq()
		httpInterceptors.apply {
			addAll(0, httpConfig.httpInterceptors)
			if (asDownload) {
				val range = downParam.desSource.let { if (it.exists()) it.length() else 0 }
				add(DownloadHttpInterceptor(downParam.breakPoint, range))
			}
			add(LaunchHttpInterceptor(httpHandler))
		}
		return RealHttpEmitter(HttpInterceptorChain(httpInterceptors, 0, httpReq), httpReq)
	}

	fun asRaw(): HttpRawResult {
		val httpReq = generateHttpReq()
		return HttpRawResult(generateRealHttpEmitter())
	}

	inline fun <reified T> asResult(noinline respAction: (String?) -> String? = { it }): HttpRespResult<T> {
		val httpReq = generateHttpReq()
		return HttpRespResult(generateRealHttpEmitter(), this.httpConfig.resDataConverter, object : TypeTaken<T>() {}.type, respAction)
	}

	fun asStream(): HttpStreamResult {
		val httpReq = generateHttpReq()
		return HttpStreamResult(generateRealHttpEmitter(), downParam, downloadHandler)
	}

	fun cancel() {
		httpHandler.cancel()
		if (asDownload) {
			downloadHandler.cancel()
		}
	}
}