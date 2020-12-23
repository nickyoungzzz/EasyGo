package com.nick.easygo.result

import com.nick.easygo.converter.ResDataConverter
import java.lang.reflect.Type

class HttpRespResult constructor(private val httpResp: HttpResp, private val resDataConverter: ResDataConverter, private val respType: Type, private val respAction: (String?) -> String?) {

	fun <T> asHttpResult(): HttpResult<T> {
		var resp: String? = null
		var throwable: Throwable? = null
		when {
			httpResp.exception != null -> throwable = httpResp.exception
			httpResp.isSuccessful -> resp = httpResp.resp
			!httpResp.isSuccessful -> throwable = HttpError(httpResp.resp)
		}
		return HttpResult(httpResp.code, httpResp.headers, httpResp.url, resDataConverter.convert(respAction.invoke(resp), respType), throwable)
	}
}

data class HttpResult<T> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, val res: T?, val error: Throwable?)

data class HttpError(val error: String) : Throwable(error)


