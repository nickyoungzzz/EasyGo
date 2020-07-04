package com.nick.easyhttp.core.req.okhttp

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.core.ReqMethod
import com.nick.easyhttp.core.req.HttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class OkHttpHandler : HttpHandler {

	private lateinit var call: Call

	override fun execute(httpReq: HttpReq): HttpResp {
		val httpReqHead = httpReq.httpReqHead
		call = EasyHttp.okHttpClient.newCall(proceed(httpReq.newBuilder().httpReqHead(httpReqHead.newBuilder().addHeader("client-type", "OkHttp").build()).build()))
		return try {
			val response = call.execute()
			val responseBody = response.body as ResponseBody
			val resp = if (!httpReq.asDownload) responseBody.string() else ""
			HttpResp(resp, response.code, response.isSuccessful, response.headers.toMultimap(), null,
				responseBody.contentLength(), responseBody.byteStream())
		} catch (e: IOException) {
			HttpResp("", 0, false, emptyMap(), null, 0, null)
		}
	}

	override fun cancel() {
		if (call.isExecuted() && !call.isCanceled()) {
			call.cancel()
		}
	}

	private fun proceed(httpReq: HttpReq): Request {
		val jsonBody = httpReq.httpReqBody.jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
		val body = if (httpReq.httpReqBody.isMultiPart) multiPart(httpReq.httpReqBody.multipartBody) else form(httpReq.httpReqBody.fieldMap)
		return Request.Builder().tag(httpReq.reqTag).apply {
			when (httpReq.reqMethod) {
				ReqMethod.POST -> post(jsonBody)
				ReqMethod.GET_FORM, ReqMethod.GET -> {
					val httpReqHeadBuilder = httpReq.httpReqHead.newBuilder()
					httpReq.httpReqBody.fieldMap.forEach { (key, value) ->
						httpReqHeadBuilder.addHeader(key, value)
					}
					get()
				}
				ReqMethod.POST_FORM -> post(body)
				ReqMethod.PUT -> put(jsonBody)
				ReqMethod.DELETE -> delete(jsonBody)
				ReqMethod.PUT_FORM -> put(body)
				ReqMethod.DELETE_FORM -> delete(body)
				ReqMethod.PATCH -> patch(jsonBody)
				ReqMethod.PATCH_FORM -> patch(body)
				ReqMethod.HEAD -> head()
			}.apply {
				httpReq.httpReqHead.headerMap.forEach { (key, value) -> addHeader(key, value) }
				val regex = if (httpReq.url.contains("?")) "&" else "?"
				val stringBuilder = StringBuilder(if (httpReq.httpReqHead.queryMap.isNotEmpty()) regex else "")
				httpReq.httpReqHead.queryMap.forEach { (key, value) ->
					stringBuilder.append("$key=$value&")
				}
				url("${httpReq.url}${stringBuilder.toString().substringBeforeLast("&")}")
			}
		}.cacheControl(CacheControl.parse(httpReq.httpReqHead.headerMap.toHeaders())).build()
	}

	// 获取表单请求的RequestBody
	private fun form(fieldMap: Map<String, String>): FormBody {
		val formBodyBuilder = FormBody.Builder()
		if (!fieldMap.isNullOrEmpty()) {
			fieldMap.forEach {
				formBodyBuilder.addEncoded(it.key, it.value)
			}
		}
		return formBodyBuilder.build()
	}

	// 获取多请求体的RequestBody
	private fun multiPart(multipartBodyMap: Map<String, Any>): MultipartBody {
		val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
		multipartBodyMap.forEach {
			if (it.value is String) {
				multipartBody.addFormDataPart(it.key, it.value as String)
			} else if (it.value is File) {
				val requestBody = (it.value as File).asRequestBody("Content-Type: application/octet-stream".toMediaTypeOrNull())
				multipartBody.addFormDataPart(it.key, (it.value as File).name, requestBody)
			}
		}
		return multipartBody.build()
	}
}
