package com.nick.easyhttp.core.req

import com.nick.easyhttp.config.OkhttpConfig
import com.nick.easyhttp.enums.ReqMethod
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class OkhttpHandler : IHttpHandler {

	private var call: Call? = null

	private val okHttpClient by lazy {
		OkhttpConfig.okHttpClient
			?: throw RuntimeException("please config EasyHttp first!!!")
	}

	override fun execute(httpReq: HttpReq): HttpResp {
		call = okHttpClient.newCall(request(reqConfig(httpReq)))
		val httpRespBuilder = HttpResp.Builder()
		try {
			val response = call?.execute()
			val responseBody = response?.body
			val resp = if (!httpReq.asDownload) responseBody?.string() else ""
			httpRespBuilder.isSuccessful(response?.isSuccessful!!)
				.code(response.code)
				.headers(response.headers.toMutableList())
				.contentLength(responseBody?.contentLength()!!)
				.byteData(responseBody.byteStream())
				.resp(resp)
		} catch (e: IOException) {
			httpRespBuilder.isSuccessful(false).exception(e).build()
		}
		return httpRespBuilder.build()
	}

	override fun reqConfig(httpReq: HttpReq): HttpReq {
		return httpReq.apply {
			headerMap["hello"] = "world"
			headerMap.remove("name")
		}
	}

	override fun cancel() {
		if (call?.isExecuted()!! && !call?.isCanceled()!!) {
			call?.cancel()
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
						httpReq.headerMap[key] = value
					}
					get()
				}
				ReqMethod.POST_FORM -> post(body)
				ReqMethod.PUT -> put(jsonBody)
				ReqMethod.DELETE -> delete(jsonBody)
				ReqMethod.PUT_FORM -> put(body)
				ReqMethod.DELETE_FORM -> delete(body)
			}.apply {
				httpReq.headerMap.forEach { (key, value) -> addHeader(key, value) }
				val regex = if (httpReq.url.contains("?")) "&" else "?"
				val stringBuilder = StringBuilder(if (httpReq.queryMap.isNotEmpty()) regex else "")
				httpReq.queryMap.forEach { (key, value) ->
					stringBuilder.append("$key=$value&")
				}
				url("${httpReq.url}${stringBuilder.toString().substringBeforeLast("&")}")
			}
		}.build()
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

	// 获取查询的header
	private fun queryHeader(queryHeader: HashMap<String, String>): String {
		val stringBuilder = StringBuilder()
		queryHeader.forEach { (key, value) ->
			stringBuilder.append("$key=$value")
		}
		return stringBuilder.toString()
	}
}
