package com.nick.easygo.result

import com.nick.easygo.converter.ResDataConverter
import com.nick.easygo.util.reflect.TypeTaken

class HttpRespResult internal constructor(val httpResp: HttpResp, val resDataConverter: ResDataConverter) {

	inline fun <reified T> asHttpResult(resAction: (String?) -> String? = { it }): HttpResult<T> {
		var resp: String? = null
		var throwable: Throwable? = null
		when {
			httpResp.exception != null -> throwable = httpResp.exception
			httpResp.isSuccessful -> resp = httpResp.resp
			!httpResp.isSuccessful -> throwable = HttpError(httpResp.resp)
		}
		val type = object : TypeTaken<T>() {}.type
		return HttpResult(httpResp.code, httpResp.headers, httpResp.url, resDataConverter.convert(resAction.invoke(resp), type), throwable)
	}
}

data class HttpResult<T> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, val res: T?, val error: Throwable?)

data class HttpError(val error: String) : Throwable(error)


