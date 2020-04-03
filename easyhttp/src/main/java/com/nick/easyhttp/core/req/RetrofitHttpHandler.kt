package com.nick.easyhttp.core.req

import com.nick.easyhttp.config.RetrofitConfig
import com.nick.easyhttp.enums.HttpProtocol
import com.nick.easyhttp.enums.ReqMethod
import com.nick.easyhttp.internal.HttpService
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.io.IOException

class RetrofitHttpHandler : IHttpHandler {

	private var call: Call<ResponseBody>? = null

	private val retrofit by lazy {
		RetrofitConfig.retrofit
			?: throw RuntimeException("please config EasyHttp first!!!")
	}

	override fun execute(httpReq: HttpReq): HttpResp {
		val httpRespBuilder = HttpResp.Builder()
		try {
			val response = call(httpReq).execute()
			val responseBody = response.body()
			val resp = if (response.isSuccessful && !httpReq.asDownload) responseBody?.string() else response.errorBody()?.string()
			httpRespBuilder.isSuccessful(response.isSuccessful)
				.code(response.code())
				.headers(response.headers().toList())
				.contentLength(responseBody?.contentLength()!!)
				.byteData(responseBody.byteStream())
				.resp(resp)
		} catch (e: IOException) {
			httpRespBuilder.isSuccessful(false).exception(e).build()
		}
		return httpRespBuilder.build()
	}

	override fun cancel() {
		if (call?.isExecuted!! && !call?.isCanceled!!) {
			call?.cancel()
		}
	}

	private fun call(httpReq: HttpReq): Call<ResponseBody> {
		val reqUrl = httpReq.url
		val reqMethod = httpReq.reqMethod
		val reqTag = httpReq.reqTag
		val queryMap = httpReq.queryMap
		val fieldMap = httpReq.fieldMap
		val headerMap = httpReq.headerMap
		val multipartBody = httpReq.multipartBody
		val isMultiPart = httpReq.isMultiPart
		val jsonString = httpReq.jsonString
		val realHttpUrl = reqUrl.startsWith(HttpProtocol.HTTP.schema, true) or reqUrl.startsWith(HttpProtocol.HTTPS.schema, true)
		val url = if (realHttpUrl) reqUrl else retrofit.baseUrl().toString().plus(reqUrl)
		val httpService = retrofit.create(HttpService::class.java)
		val requestBody = jsonString.toRequestBody("Content-Type:application/json;charset=utf-8".toMediaTypeOrNull())
		call = when (reqMethod) {
			ReqMethod.GET ->
				if (httpReq.asDownload) httpService.getDownload(url, headerMap, queryMap, reqTag)
				else httpService.get(url, headerMap, queryMap, reqTag)
			ReqMethod.POST ->
				if (httpReq.asDownload) httpService.postDownload(url, headerMap, queryMap, requestBody, reqTag)
				else httpService.post(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.GET_FORM -> httpService.getForm(url, headerMap, queryMap, fieldMap, reqTag)
			ReqMethod.POST_FORM -> httpService.post(url, headerMap, queryMap,
				if (isMultiPart) constructMultiPartBody(multipartBody) else constructFormBody(fieldMap), reqTag)
			ReqMethod.PUT -> httpService.put(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.DELETE -> httpService.delete(url, headerMap, queryMap, requestBody, reqTag)
			ReqMethod.PUT_FORM -> httpService.put(url, headerMap, queryMap,
				if (isMultiPart) constructMultiPartBody(multipartBody) else constructFormBody(fieldMap), reqTag)
			ReqMethod.DELETE_FORM -> httpService.delete(url, headerMap, queryMap,
				if (isMultiPart) constructMultiPartBody(multipartBody) else constructFormBody(fieldMap), reqTag)
		}
		return call as Call<ResponseBody>
	}

	private fun constructFormBody(fieldMap: HashMap<String, String>): FormBody {
		val formBodyBuilder = FormBody.Builder()
		if (!fieldMap.isNullOrEmpty()) {
			fieldMap.forEach {
				formBodyBuilder.addEncoded(it.key, it.value)
			}
		}
		return formBodyBuilder.build()
	}

	private fun constructMultiPartBody(multipartBodyMap: HashMap<String, Any>): MultipartBody {
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
