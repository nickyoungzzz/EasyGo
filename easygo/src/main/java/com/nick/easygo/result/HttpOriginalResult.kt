package com.nick.easygo.result

class HttpOriginalResult internal constructor(private val httpResp: HttpResp) {

	fun originalResult(block: (HttpResp) -> Unit): HttpOriginalResult {
		block.invoke(httpResp)
		return this
	}

	fun <T, F> mapResult(mapResult: HttpResult<T, F>.() -> Unit): HttpResult<T, F> {
		var resp: String? = null
		var throwable: Throwable? = null
		when {
			httpResp.exception != null -> throwable = httpResp.exception
			else -> if (httpResp.isSuccessful) resp = httpResp.resp else HttpError(httpResp.resp)
		}
		return HttpResult<T, F>(httpResp.code, httpResp.headers, httpResp.url, resp, throwable).apply(mapResult)
	}
}

class HttpResult<T, F> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, private val resp: String?, private val throwable: Throwable?) {

	var result: T? = resp?.let { it as? T }
	var error: F? = throwable?.let { it as? F }

	fun result(r: (String?) -> T?) {
		result = r.invoke(resp)
	}

	fun error(e: (Throwable?) -> F?) {
		error = e.invoke(throwable)
	}
}

class HttpError(val resp: String) : Throwable(resp)