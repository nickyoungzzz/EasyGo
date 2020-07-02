package com.nick.easyhttp.result

import com.nick.easyhttp.core.HttpStatus

class HttpResult internal constructor(builder: Builder) {

	val code: Int = builder.code
	val headers: Map<String, List<String>> = builder.headers
	val resp: String = builder.resp
	var throwable: Throwable? = builder.throwable
	var httpStatus: HttpStatus = builder.httpStatus

	fun newBuilder() = Builder(this)

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

	fun success(t: () -> Unit) {
		if (httpStatus == HttpStatus.SUCCESS) t()
	}

	fun error(f: () -> Unit) {
		if (httpStatus == HttpStatus.ERROR) f()
	}

	fun exception(e: () -> Unit) {
		if (httpStatus == HttpStatus.EXCEPTION) e()
	}

	fun <T, F, E> result(s: Result<T, F, E>.() -> Unit): Result<T, F, E> {
		return Result<T, F, E>(code, headers, resp, throwable, httpStatus).apply(s)
	}
}

class Result<T, F, E> constructor(var code: Int, var headers: Map<String, List<String>>, private var resp: String, private var throwable: Throwable?, private var httpStatus: HttpStatus) {

	var success: T? = null
	var error: F? = null
	var exception: E? = null
	fun success(t: (r: String) -> T) {
		success = if (httpStatus == HttpStatus.SUCCESS) t(resp) else null
	}

	fun error(f: (r: String) -> F) {
		error = if (httpStatus == HttpStatus.ERROR) f(resp) else null
	}

	fun exception(e: (r: Throwable?) -> E) {
		exception = if (httpStatus == HttpStatus.EXCEPTION) e(throwable) else null
	}
}
