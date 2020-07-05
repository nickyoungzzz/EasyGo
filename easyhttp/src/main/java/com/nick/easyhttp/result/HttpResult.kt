package com.nick.easyhttp.result

import com.nick.easyhttp.core.HttpStatus

class HttpResult internal constructor(val code: Int, val headers: Map<String, List<String>>, val resp: String, val throwable: Throwable?, val httpStatus: HttpStatus) {

	fun success(t: () -> Unit) {
		if (httpStatus == HttpStatus.SUCCESS) t()
	}

	fun error(f: () -> Unit) {
		if (httpStatus == HttpStatus.ERROR) f()
	}

	fun exception(e: () -> Unit) {
		if (httpStatus == HttpStatus.EXCEPTION) e()
	}

	fun <T, F, E> analysis(s: Result<T, F, E>.() -> Unit): Result<T, F, E> {
		return Result<T, F, E>(code, headers, resp, throwable, httpStatus).apply(s)
	}
}

@Suppress("UNCHECKED_CAST")
class Result<T, F, E> constructor(val code: Int, val headers: Map<String, List<String>>, private val resp: String, private val throwable: Throwable?, private val httpStatus: HttpStatus) {

	var success: T? = if (httpStatus == HttpStatus.SUCCESS) resp as T else null
	var error: F? = if (httpStatus == HttpStatus.ERROR) resp as F else null
	var exception: E? = if (httpStatus == HttpStatus.ERROR) throwable as E else null

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
