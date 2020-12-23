package com.nick.easygo.core

import com.nick.easygo.config.EasyGo
import com.nick.easygo.config.HttpConfig
import com.nick.easygo.core.download.DownParam
import com.nick.easygo.core.download.DownState
import com.nick.easygo.core.download.DownloadHandler
import com.nick.easygo.core.interceptor.DownloadHttpInterceptor
import com.nick.easygo.core.interceptor.HttpInterceptor
import com.nick.easygo.core.interceptor.HttpInterceptorChain
import com.nick.easygo.core.interceptor.LaunchHttpInterceptor
import com.nick.easygo.core.param.HttpParam
import com.nick.easygo.core.req.HttpHandler
import com.nick.easygo.result.*
import com.nick.easygo.util.reflect.TypeTaken
import java.io.IOException

class HttpEmitter internal constructor(private val param: HttpParam) {

	private var reqTag: Any? = null

	private var asDownload = false

	var httpConfig: HttpConfig = EasyGo.httpConfig

	private var httpHandler: HttpHandler = httpConfig.httpHandler

	private var downloadHandler: DownloadHandler = httpConfig.downLoadHandler

	private lateinit var downParam: DownParam

	private val httpInterceptors = ArrayList<HttpInterceptor>()

	fun tag(reqTag: Any?) = apply {
		this.reqTag = reqTag
	}

	fun asDownload(down: DownParam.() -> Unit) = apply {
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

	private fun generateHttpReq(): HttpReq {
		return HttpReq(
			param.url, param.reqMethod, this@HttpEmitter.reqTag, param.headerMap, param.queryMap,
			HttpReqBody(param.fieldMap, param.multipartBody, param.isMultiPart, param.jsonString), this@HttpEmitter.asDownload
		).newBuilder().addHeader("request-client", httpHandler.requestClient).build()
	}

	fun generateHttpResp(): HttpResp {
		val originalHttpReq = generateHttpReq()
		httpInterceptors.apply {
			addAll(0, httpConfig.httpInterceptors)
			if (asDownload) {
				val range = downParam.desSource.let { if (it.exists()) it.length() else 0 }
				add(DownloadHttpInterceptor(downParam.breakPoint, range))
			}
			add(LaunchHttpInterceptor(httpHandler))
		}
		return HttpInterceptorChain(httpInterceptors, 0, originalHttpReq).proceed(originalHttpReq)
	}

	inline fun <reified T> send(noinline respAction: (String?) -> String? = { it }): HttpResult<T> {
		return HttpRespResult(generateHttpResp(), this.httpConfig.resDataConverter, object : TypeTaken<T>() {}.type, respAction).asHttpResult()
	}

	fun download(exc: (e: Throwable) -> Unit = {}, download: (downState: DownState) -> Unit = {}) {
		generateHttpResp().let {
			if (it.isSuccessful) {
				try {
					downloadHandler.saveFile(it.inputStream!!, downParam, it.contentLength) { state ->
						download(state)
					}
				} catch (e: IOException) {
					exc(e)
				}
			} else {
				it.exception?.run {
					exc(this)
				}
			}
		}
	}

	fun cancel() {
		httpHandler.cancel()
		if (asDownload) {
			downloadHandler.cancel()
		}
	}
}