package com.nick.easygo.result

import com.nick.easygo.converter.ResDataConverter
import com.nick.easygo.core.download.DownParam
import com.nick.easygo.core.download.DownState
import com.nick.easygo.core.download.DownloadHandler
import com.nick.easygo.core.res.HttpExecutor
import com.nick.easygo.core.res.HttpSender
import com.nick.easygo.core.res.HttpStreamer
import com.nick.easygo.core.res.RealHttpEmitter
import java.io.IOException
import java.lang.reflect.Type

open class HttpRawResult constructor(private val realHttpEmitter: RealHttpEmitter) : HttpExecutor {
	override fun execute(): HttpResp {
		return realHttpEmitter.emit()
	}
}

class HttpRespResult<T> constructor(
	realHttpEmitter: RealHttpEmitter, private val resDataConverter: ResDataConverter,
	private val respType: Type, private val respAction: (String?) -> String?,
) : HttpSender<T>, HttpRawResult(realHttpEmitter) {
	override fun send(): HttpResult<T> {
		val httpResp = execute()
		return HttpResult(httpResp.code, httpResp.headers, httpResp.url, resDataConverter.convert(respAction.invoke(httpResp.res), respType), httpResp.err)
	}
}

class HttpStreamResult(
	realHttpEmitter: RealHttpEmitter,
	private val downParam: DownParam, private val downloadHandler: DownloadHandler,
) : HttpStreamer, HttpRawResult(realHttpEmitter) {
	override fun download(exc: ((e: Throwable) -> Unit)?, download: ((downState: DownState) -> Unit)?) {
		val httpResp = execute()
		if (httpResp.isSuccessful) {
			try {
				downloadHandler.saveFile(httpResp.inputStream!!, downParam, httpResp.contentLength) { state ->
					download?.invoke(state)
				}
			} catch (e: IOException) {
				exc?.invoke(e)
			}
		} else {
			httpResp.err?.let {
				exc?.invoke(it)
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


