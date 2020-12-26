package com.nick.easygo.result

import com.nick.easygo.converter.ResDataConverter
import com.nick.easygo.core.HttpSender
import com.nick.easygo.core.download.DownParam
import com.nick.easygo.core.download.DownState
import com.nick.easygo.core.download.DownloadHandler
import java.io.IOException
import java.lang.reflect.Type

class HttpRespResult<T> constructor(
	private val httpResp: HttpResp, private val resDataConverter: ResDataConverter,
	private val respType: Type, private val respAction: (String?) -> String?,
	private val downParam: DownParam, private val downloadHandler: DownloadHandler,
) : HttpSender<T> {

	override fun send(): HttpResult<T> {
		return HttpResult(httpResp.code, httpResp.headers, httpResp.url, resDataConverter.convert(respAction.invoke(httpResp.res), respType), httpResp.err)
	}

	override fun download(exc: ((e: Throwable) -> Unit)?, download: ((downState: DownState) -> Unit)?) {
		if (httpResp.isSuccessful) {
			try {
				downloadHandler.saveFile(httpResp.inputStream!!, downParam, httpResp.contentLength) { state ->
					download?.invoke(state)
				}
			} catch (e: IOException) {
				exc?.invoke(e)
			}
		} else {
			httpResp.exception?.run {
				exc?.invoke(this)
			}
		}
	}

}

data class HttpResult<T> constructor(val code: Int, val headers: Map<String, List<String>>, val url: String, val res: T?, val error: Throwable?)

data class HttpError(val error: String) : Throwable(error)

private val HttpResp.res: String?
	get() = if (!isSuccessful || exception != null) null else resp

private val HttpResp.err: Throwable?
	get() = when {
		exception != null -> exception
		!isSuccessful -> HttpError(resp)
		else -> null
	}


