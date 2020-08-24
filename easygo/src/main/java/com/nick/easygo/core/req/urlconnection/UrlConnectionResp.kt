package com.nick.easygo.core.req.urlconnection

import java.io.InputStream

class UrlConnectionResp internal constructor(builder: Builder) {

	val resp: String = builder.resp
	val code: Int = builder.code
	val isSuccessful: Boolean = builder.isSuccessful
	val headers: Map<String, List<String>> = builder.headers
	val exception = builder.exception
	val contentLength = builder.contentLength
	val inputStream: InputStream? = builder.inputStream
	val url: String = builder.url

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var resp: String = ""
		internal var code: Int = 0
		internal var isSuccessful = true
		internal var headers: Map<String, List<String>> = hashMapOf()
		internal var exception: Throwable? = null
		internal var contentLength: Long = 0
		internal var inputStream: InputStream? = null
		internal var url: String = ""

		internal constructor(urlConnectionResp: UrlConnectionResp) : this() {
			this.resp = urlConnectionResp.resp
			this.code = urlConnectionResp.code
			this.isSuccessful = urlConnectionResp.isSuccessful
			this.headers = urlConnectionResp.headers
			this.exception = urlConnectionResp.exception
			this.contentLength = urlConnectionResp.contentLength
			this.inputStream = urlConnectionResp.inputStream
			this.url = urlConnectionResp.url
		}

		fun resp(resp: String) = apply {
			this.resp = resp
		}

		fun code(code: Int) = apply {
			this.code = code
		}

		fun isSuccessful(isSuccessful: Boolean) = apply {
			this.isSuccessful = isSuccessful
		}

		fun headers(headers: Map<String, List<String>>) = apply {
			this.headers = headers
		}

		fun exception(exception: Exception) = apply {
			this.exception = exception
		}

		fun contentLength(contentLength: Long) = apply {
			this.contentLength = contentLength
		}

		fun byteData(inputStream: InputStream) = apply {
			this.inputStream = inputStream
		}

		fun url(url: String) = apply {
			this.url = url
		}

		fun build(): UrlConnectionResp = UrlConnectionResp(this)
	}
}