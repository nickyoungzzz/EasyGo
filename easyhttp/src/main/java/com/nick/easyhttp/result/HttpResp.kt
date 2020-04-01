package com.nick.easyhttp.result

class HttpResp internal constructor(builder: Builder) {

	val resp: String? = builder.resp
	val code: Int = builder.code
	val isSuccessful: Boolean = builder.isSuccessful
	val headers: List<Pair<String, String>>? = builder.headers
	val exception = builder.exception

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var resp: String? = null
		internal var code: Int = 200
		internal var isSuccessful = true
		internal var headers: List<Pair<String, String>>? = null
		internal var exception: Exception? = null

		internal constructor(httpResp: HttpResp) : this() {
			this.resp = httpResp.resp
			this.code = httpResp.code
			this.isSuccessful = httpResp.isSuccessful
			this.headers = httpResp.headers
			this.exception = httpResp.exception
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

		fun build(): HttpResp = HttpResp(this)
	}
}