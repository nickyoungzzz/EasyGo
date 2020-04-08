package com.nick.easyhttp.core.req

import com.nick.easyhttp.core.ReqMethod
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import okio.buffer
import okio.sink
import okio.source
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
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
		val url = URL("${httpReq.url}${stringBuilder.toString().substringBeforeLast("&").toByteArray()}")
		val httpURLConnection = url.openConnection() as HttpURLConnection
		httpURLConnection.requestMethod = httpReq.reqMethod.method
		httpURLConnection.doOutput = true
		val outputStream = httpURLConnection.outputStream
		httpURLConnection.disconnect()
		stringBuilder.clear()
		when (httpReq.reqMethod) {
			ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD -> {
			}
			ReqMethod.POST, ReqMethod.PUT, ReqMethod.DELETE, ReqMethod.PATCH -> outputStream.write(httpReq.jsonString.toByteArray())
			ReqMethod.POST_FORM -> {
				if (httpReq.isMultiPart) {
					val end = "/r/n"
					val twoHyphens = "--"
					val boundary = "*****"
					httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
					val dataOutputStream = DataOutputStream(outputStream)
					httpReq.multipartBody.forEach { (key, value) ->
						dataOutputStream.writeBytes("$end$twoHyphens$boundary")
						dataOutputStream.writeBytes("Content-Disposition: form-data; $key:${value.run {
							if (this is File) run {
								outputStream.sink().buffer().writeAll(FileInputStream(this).source())
								this.absolutePath
							} else this
						}
						};$end")
						dataOutputStream.writeBytes("$end$twoHyphens$boundary")
					}
					dataOutputStream.flush()
					dataOutputStream.close()
				} else {
					httpReq.fieldMap.forEach { (key, value) ->
						stringBuilder.append("$key=$value&")
					}
					outputStream.write(stringBuilder.toString().substringBeforeLast("&").toByteArray())
				}
			}
			ReqMethod.PUT_FORM, ReqMethod.DELETE_FORM, ReqMethod.PATCH_FORM -> {
				throw RuntimeException("put, delete, patch method do not have multipart body or form body")
			}
		}
		outputStream.flush()
		outputStream.close()
		httpURLConnection.connect()
		val success = httpURLConnection.responseCode in 200..299
		val inputStream = if (success) httpURLConnection.inputStream else httpURLConnection.errorStream
		val resp = inputStream.readBytes().toString(Charset.defaultCharset())
		inputStream.close()
		httpURLConnection.disconnect()
		return HttpResp.Builder()
			.code(httpURLConnection.responseCode)
			.byteData(inputStream)
			.isSuccessful(success)
			.headers(httpURLConnection.headerFields)
			.contentLength(httpURLConnection.contentLength.toLong())
			.resp(resp)
			.build()
	}

	override fun reqConfig(httpReq: HttpReq): HttpReq {
		return httpReq
	}

	override fun cancel() {
	}
}