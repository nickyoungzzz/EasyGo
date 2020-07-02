package com.nick.easyhttp.core

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.config.HttpConfig
import com.nick.easyhttp.core.download.DownloadHandler
import com.nick.easyhttp.core.download.DownloadParam
import com.nick.easyhttp.core.download.DownloadState
import com.nick.easyhttp.core.param.HttpParam
import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import com.nick.easyhttp.result.HttpResult
import java.io.IOException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class HttpSend internal constructor(var param: HttpParam) {

	private var reqTag: Any? = null

	private var asDownload = false

	private var httpConfig: HttpConfig = EasyHttp.httpConfig

	private var httpHandler: HttpHandler = httpConfig.httpHandler

	private var downloadHandler: DownloadHandler = httpConfig.downLoadHandler

	private lateinit var downloadParam: DownloadParam

	fun tag(reqTag: Any) {
		this.reqTag = reqTag
	}

	fun asDownload(down: DownloadParam.() -> Unit) {
		this.asDownload = true
		this.downloadParam = DownloadParam().apply(down)
	}

	fun httpHandler(httpHandler: HttpHandler) {
		this.httpHandler = httpHandler
	}

	private var beforeExecute = fun(httpReq: HttpReq): HttpReq = httpReq

	private var afterExecute = fun(_: HttpReq, httpResp: HttpResp) = httpResp

	fun beforeSend(before: (httpReq: HttpReq) -> HttpReq) = apply { this.beforeExecute = before }

	fun afterReply(after: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp) = apply { this.afterExecute = after }

	private fun getProxyHttpHandler(): HttpHandler {
		return Proxy.newProxyInstance(httpHandler.javaClass.classLoader, httpHandler.javaClass.interfaces,
			HttpInvocation(httpHandler, beforeExecute, afterExecute, httpReq())) as HttpHandler
	}

	fun downloadHandler(downloadHandler: DownloadHandler) {
		this.downloadHandler = downloadHandler
	}

	private fun httpReq(): HttpReq {
		return HttpReq.Builder().apply {
			url(param.url)
			reqMethod(param.reqMethod)
			reqTag(reqTag)
			queryMap(param.queryMap)
			fieldMap(param.fieldMap)
			headerMap(param.headerMap)
			multipartBody(param.multipartBody)
			isMultiPart(param.isMultiPart)
			jsonString(param.jsonString)
			asDownload(asDownload)
		}.build()
	}

	fun extra(extra: HttpSend.() -> Unit) = apply(extra)

	fun send(init: HttpResult.() -> Unit = {}): HttpResult {
		val httpReq = httpConfig.before(httpReq())
		val httpResp = httpConfig.after(httpReq, getProxyHttpHandler().execute(httpReq))
		val status = if (httpResp.exception != null) HttpStatus.EXCEPTION
		else (if (httpResp.isSuccessful) HttpStatus.SUCCESS else HttpStatus.ERROR)
		return HttpResult.Builder().code(httpResp.code)
			.headers(httpResp.headers)
			.resp(httpResp.resp)
			.throwable(httpResp.exception)
			.status(status)
			.build()
			.apply(init)
	}

	@JvmOverloads
	fun download(exc: (e: Throwable) -> Unit = {}, download: (downloadState: DownloadState) -> Unit = {}) = apply {
		val source = downloadParam.desSource
		val range = if (downloadParam.breakPoint && source.exists()) source.length() else 0
		val httpResp = getProxyHttpHandler().execute(httpReq().apply { headerMap["Range"] = "bytes=${range}-" })
		if (httpResp.isSuccessful) {
			try {
				downloadHandler.saveFile(httpResp.inputStream!!, downloadParam, httpResp.contentLength) { state ->
					download(state)
				}
			} catch (e: IOException) {
				exc(e)
			}
		} else {
			exc(httpResp.exception!!)
		}
		return this
	}

	fun cancelRequest() {
		httpHandler.cancel()
		if (asDownload) {
			downloadHandler.cancel()
		}
	}

	internal class HttpInvocation internal constructor(private val any: Any, private val before: (httpReq: HttpReq) -> HttpReq,
	                                                   private val after: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp,
	                                                   private val httpReq: HttpReq
	) : InvocationHandler {
		override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
			return if (method?.name == "execute") {
				val req = before(httpReq)
				val obj = method.invoke(any, req)
				after(req, obj as HttpResp)
			} else {
				method!!.invoke(any, args)
			}
		}
	}
}