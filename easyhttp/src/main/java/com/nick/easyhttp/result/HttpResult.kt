package com.nick.easyhttp.result

class HttpResult internal constructor(builder: Builder) {

	val code: Int = builder.code
	val headers: Map<String, List<String>> = builder.headers
	val resp: String = builder.resp
	var throwable: Throwable? = builder.throwable
	var httpStatus: HttpStatus = builder.httpStatus

	fun newBuilder() = Builder(this)

	private fun isSuccess() = httpStatus == HttpStatus.SUCCESS

	private fun isError() = httpStatus == HttpStatus.ERROR

	private fun isException() = httpStatus == HttpStatus.EXCEPTION

	class Builder constructor() {

		internal var code = 0
		internal var headers: Map<String, List<String>> = hashMapOf()
		internal var resp = ""
		internal var throwable: Throwable? = null
		internal var httpStatus = HttpStatus.EXCEPTION

		internal constructor(httpResult: HttpResult) : this() {
			this.code = httpResult.code
			this.headers = httpResult.headers
			this.resp = httpResult.resp
			this.throwable = httpResult.throwable
			this.httpStatus = httpResult.httpStatus
		}

		fun code(code: Int) = apply { this.code = code }

		fun headers(headers: Map<String, List<String>>) = apply { this.headers = headers }

		fun resp(resp: String) = apply { this.resp = resp }

		fun throwable(throwable: Throwable?) = apply { this.throwable = throwable }

		fun status(httpStatus: HttpStatus) = apply { this.httpStatus = httpStatus }

		fun build(): HttpResult = HttpResult(this)
	}

	fun success(block: (string: String) -> Unit) = apply { if (isSuccess()) block(resp) }

	fun error(block: (string: String) -> Unit) = apply { if (isError()) block(resp) }

	fun exception(block: (e: Throwable?) -> Unit) = apply { if (isException()) block(throwable) }

	fun <T> getSuccess(t: (string: String) -> T?): T? = if (isSuccess()) t(resp) else null

	fun <F> getError(t: (string: String) -> F?): F? = if (isError()) t(resp) else null

	fun getException(t: (exception: Throwable?) -> Throwable): Throwable? = if (isException()) t(throwable) else null

	fun getSuccessString(): String? = if (isSuccess()) resp else null

	fun getErrorString(): String? = if (isError()) resp else null

	fun getException(): Throwable? = if (isException()) throwable else null

	enum class HttpStatus {
		SUCCESS, ERROR, EXCEPTION
	}
}
