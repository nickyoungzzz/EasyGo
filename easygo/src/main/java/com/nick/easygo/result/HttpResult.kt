package com.nick.easygo.result

import com.nick.easygo.parse.ResDataConverter
import java.lang.reflect.ParameterizedType

class HttpRawResult internal constructor(val httpResp: HttpResp, val resDataConverter: ResDataConverter) {

	fun rawResult(block: (HttpResp) -> Unit): HttpRawResult {
		block.invoke(httpResp)
		return this
	}
}

inline fun <reified T> HttpRawResult.asHttpResult(resAction: (String?) -> String? = { it }): HttpResult<T> {
	var resp: String? = null
	var throwable: Throwable? = null
	when {
		httpResp.exception != null -> throwable = httpResp.exception
		httpResp.isSuccessful -> resp = httpResp.resp
		!httpResp.isSuccessful -> throwable = HttpError(httpResp.resp)
	}
	val type = (T::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0]
	return HttpResult(httpResp.code, httpResp.headers, httpResp.url, resDataConverter.convert(resAction.invoke(resp), type), throwable)
}

data class HttpResult<T> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, val res: T?, val error: Throwable?)

data class HttpError(val error: String) : Throwable(error)


