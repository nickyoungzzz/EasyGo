package com.nick.easyhttp.core.req.okhttp

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.core.ReqMethod
import com.nick.easyhttp.core.req.IHttpHandler
import com.nick.easyhttp.inject.DaggerHttpHandlerComponent
import com.nick.easyhttp.inject.HttpHandlerModule
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class OkHttpHandler : IHttpHandler {

	private lateinit var call: Call

	@Inject
	lateinit var okHttpClient: OkHttpClient

	override fun execute(httpReq: HttpReq): HttpResp {
		DaggerHttpHandlerComponent.builder().httpHandlerModule(HttpHandlerModule(EasyHttp.httpConfig)).build().inject(this)
		call = okHttpClient.newCall(request(httpReq))
		val httpRespBuilder = HttpResp.Builder()
		try {
			val response = call.execute()
			val responseBody = response.body as ResponseBody
			val resp = if (!httpReq.asDownload) responseBody.string() else ""
			httpRespBuilder.isSuccessful(response.isSuccessful)
				.code(response.code)
				.headers(response.headers.toMultimap())
				.contentLength(responseBody.contentLength())
				.byteData(responseBody.byteStream())
				.resp(resp)
		} catch (e: IOException) {
			httpRespBuilder.isSuccessful(false).exception(e).build()
		}
		return httpRespBuilder.build()
	}

	override fun cancel() {
		if (call.isExecuted() && !call.isCanceled()) {
			call.cancel()
		}
	}

	private fun request(httpReq: HttpReq): Request {
		val jsonBody = httpReq.jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
		val body = if (httpReq.isMultiPart) multiPart(httpReq.multipartBody) else form(httpReq.fieldMap)
		return Request.Builder().tag(httpReq.reqTag).apply {
			when (httpReq.reqMethod) {
				ReqMethod.POST -> post(jsonBody)
				ReqMethod.GET_FORM, ReqMethod.GET -> {
					httpReq.fieldMap.forEach { (key, value) ->
						httpReq.queryMap[key] = value
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
				httpReq.headerMap.forEach { (key, value) -> addHeader(key, value) }
				val regex = if (httpReq.url.contains("?")) "&" else "?"
				val stringBuilder = StringBuilder(if (httpReq.queryMap.isNotEmpty()) regex else "")
				httpReq.queryMap.forEach { (key, value) ->
					stringBuilder.append("$key=$value&")
				}
				url("${httpReq.url}${stringBuilder.toString().substringBeforeLast("&")}")
			}
		}.cacheControl(CacheControl.parse(httpReq.headerMap.toHeaders())).build()
	}

	// 获取表单请求的RequestBody
	private fun form(fieldMap: HashMap<String, String>): FormBody {
		val formBodyBuilder = FormBody.Builder()
		if (!fieldMap.isNullOrEmpty()) {
			fieldMap.forEach {
				formBodyBuilder.addEncoded(it.key, it.value)
			}
		}
		return formBodyBuilder.build()
	}

	// 获取多请求体的RequestBody
	private fun multiPart(multipartBodyMap: HashMap<String, Any>): MultipartBody {
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
