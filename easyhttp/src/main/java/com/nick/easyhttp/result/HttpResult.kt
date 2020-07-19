package com.nick.easyhttp.result

import com.nick.easyhttp.core.HttpStatus

class HttpResult internal constructor(val url: String, val code: Int, val headers: Map<String, List<String>>, val resp: String, val throwable: Throwable?, val httpStatus: HttpStatus) {

	fun success(t: () -> Unit) {
		if (httpStatus == HttpStatus.SUCCESS) t()
	}

	fun error(f: () -> Unit) {
		if (httpStatus == HttpStatus.ERROR) f()
	}

	fun exception(e: () -> Unit) {
		if (httpStatus == HttpStatus.EXCEPTION) e()
	}

	fun <T, F, E> transform(s: Result<T, F, E>.() -> Unit): Result<T, F, E> {
		return Result<T, F, E>(code, headers, url, resp, throwable, httpStatus).apply(s)
	}
}

@Suppress("UNCHECKED_CAST")
class Result<T, F, E> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, private val resp: String, private val throwable: Throwable?, private val httpStatus: HttpStatus) {

	val isSuccess = httpStatus == HttpStatus.SUCCESS
	val isError = httpStatus == HttpStatus.ERROR
	val isException = httpStatus == HttpStatus.EXCEPTION

	var success: T? = if (isSuccess) resp as T else null
	var error: F? = if (isError) resp as F else null
	var exception: E? = if (isException) throwable as E else null

	fun success(t: (r: String) -> T) {
		success = if (isSuccess) t(resp) else null
	}

	fun error(f: (r: String) -> F) {
		error = if (isError) f(resp) else null
	}

	fun exception(e: (r: Throwable?) -> E) {
		exception = if (isException) e(throwable) else null
	}
}
