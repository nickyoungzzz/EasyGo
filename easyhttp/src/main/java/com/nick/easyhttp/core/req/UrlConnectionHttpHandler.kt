package com.nick.easyhttp.core.req

import com.nick.easyhttp.core.ReqMethod
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class UrlConnectionHttpHandler : IHttpHandler {

	override fun execute(httpReq: HttpReq): HttpResp {

		if (httpReq.reqMethod in setOf(ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD)) {
			httpReq.fieldMap.forEach { (key, value) ->
				httpReq.queryMap[key] = value
			}
		}
		val regex = if (httpReq.url.contains("?")) "&" else "?"
		val stringBuilder = StringBuilder(if (httpReq.queryMap.isNotEmpty()) regex else "")
		httpReq.queryMap.forEach { (key, value) ->
			stringBuilder.append("$key=$value&")
		}
		val url = URL("${httpReq.url}${stringBuilder.toString().substringBeforeLast("&")}")
		stringBuilder.clear()
		val httpURLConnection = url.openConnection() as HttpURLConnection
		httpURLConnection.requestMethod = httpReq.reqMethod.method
		httpURLConnection.doOutput = true
		httpURLConnection.addRequestProperty("accept-encoding", "gzip, deflate, br")
		when (httpReq.reqMethod) {
			ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD -> httpURLConnection.doOutput = false
			ReqMethod.POST, ReqMethod.PUT, ReqMethod.DELETE, ReqMethod.PATCH -> {
				val outputStream = DataOutputStream(httpURLConnection.outputStream)
				outputStream.writeBytes(httpReq.jsonString)
				outputStream.flush()
				outputStream.close()
			}
			ReqMethod.POST_FORM -> {
				val outputStream = DataOutputStream(httpURLConnection.outputStream)
				if (httpReq.isMultiPart) {
					val end = "/r/n"
					val twoHyphens = "--"
					val boundary = "*****"
					httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
					httpReq.multipartBody.forEach { (key, value) ->
						outputStream.writeBytes("$end$twoHyphens$boundary")
						outputStream.writeBytes("Content-Disposition: form-data; $key:${value.run {
							if (this is File) run {
								outputStream.write(FileInputStream(this as File).readBytes())
								this.absolutePath
							} else this
						}
						};$end")
						outputStream.writeBytes("$end$twoHyphens$boundary")
					}
				} else {
					httpReq.fieldMap.forEach { (key, value) ->
						stringBuilder.append("$key=$value&")
					}
					outputStream.writeBytes(stringBuilder.toString().substringBeforeLast("&"))
				}
				outputStream.flush()
				outputStream.close()
			}
			ReqMethod.PUT_FORM, ReqMethod.DELETE_FORM, ReqMethod.PATCH_FORM -> {
				throw RuntimeException("put, delete, patch method do not have multipart body or form body")
			}
		}
		val httpRespBuilder = HttpResp.Builder()
		try {
			httpURLConnection.connect()
			val success = httpURLConnection.responseCode in 200..299
			val inputStream = if (success) httpURLConnection.inputStream else httpURLConnection.errorStream
			val resp = if (httpReq.asDownload) "" else inputStream.readBytes().toString(Charset.defaultCharset())
			httpRespBuilder.code(httpURLConnection.responseCode)
				.byteData(inputStream)
				.isSuccessful(success)
				.headers(httpURLConnection.headerFields)
				.contentLength(httpURLConnection.contentLength.toLong())
				.resp(resp)
			if (!httpReq.asDownload) {
				inputStream.close()
			}
		} catch (e: IOException) {
			httpRespBuilder.exception(e)
		} finally {
			httpURLConnection.disconnect()
		}
		return httpRespBuilder.build()
	}

	override fun reqConfig(httpReq: HttpReq): HttpReq {
		return httpReq
	}

	override fun cancel() {
	}
}