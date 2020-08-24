package com.nick.easygo.result

import com.nick.easygo.core.HttpStatus

class HttpOriginalResult internal constructor(private val httpResp: HttpResp) {

	fun originalResult(block: (HttpResp) -> Unit): HttpOriginalResult {
		block.invoke(httpResp)
		return this
	}

	fun <T, F> transform(transform: HttpResult<T, F>.() -> Unit): HttpResult<T, F> {
		var resp: String? = null
		var throwable: Throwable? = null
		val httpStatus = when {
			httpResp.exception != null -> HttpStatus.EXCEPTION.apply {
				throwable = httpResp.exception
			}
			else -> if (httpResp.isSuccessful) HttpStatus.SUCCESS.apply {
				resp = httpResp.resp
			} else HttpStatus.ERROR.apply {
				throwable = HttpError(httpResp.resp)
			}
		}
		return HttpResult<T, F>(httpResp.code, httpResp.headers, httpResp.url, resp, throwable, httpStatus).apply(transform)
	}
}

class HttpResult<T, F> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, resp: String?, throwable: Throwable?, httpStatus: HttpStatus) {

	private var mResult: ((String?) -> T?)? = null
	private var mError: ((Throwable?) -> F?)? = null

	var result: T? = resp?.let { mResult?.invoke(it) }
	var error: F? = throwable?.let { mError?.invoke(it) }

	fun result(r: (String?) -> T?) {
		mResult = r
	}

	fun error(e: (Throwable?) -> F?) {
		mError = e
	}
}

class HttpError(message: String) : Throwable(message)