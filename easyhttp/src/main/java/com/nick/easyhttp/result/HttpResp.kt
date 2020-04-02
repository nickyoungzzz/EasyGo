package com.nick.easyhttp.result

import java.io.InputStream

class HttpResp internal constructor(builder: Builder) {

	val resp: String? = builder.resp
	val code: Int = builder.code
	val isSuccessful: Boolean = builder.isSuccessful
	val headers: List<Pair<String, String>>? = builder.headers
	val exception = builder.exception
	val contentLength = builder.contentLength
	val inputStream: InputStream? = builder.inputStream

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var resp: String? = null
		internal var code: Int = 0
		internal var isSuccessful = true
		internal var headers: List<Pair<String, String>>? = null
		internal var exception: Exception? = null
		internal var contentLength: Long = 0
		internal var inputStream: InputStream? = null

		internal constructor(httpResp: HttpResp) : this() {
			this.resp = httpResp.resp
			this.code = httpResp.code
			this.isSuccessful = httpResp.isSuccessful
			this.headers = httpResp.headers
			this.exception = httpResp.exception
			this.contentLength = httpResp.contentLength
			this.inputStream = httpResp.inputStream
		}

		fun resp(resp: String?) = apply {
			this.resp = resp
		}

		fun code(code: Int) = apply {
			this.code = code
		}

		fun isSuccessful(isSuccessful: Boolean) = apply {
			this.isSuccessful = isSuccessful
		}

		fun headers(headers: List<Pair<String, String>>) = apply {
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

		fun build(): HttpResp = HttpResp(this)
	}
}