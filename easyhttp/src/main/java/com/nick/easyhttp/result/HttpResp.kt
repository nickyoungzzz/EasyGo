package com.nick.easyhttp.result

import java.io.InputStream

class HttpResp internal constructor(val resp: String, val code: Int, val isSuccessful: Boolean,
                                    val headers: Map<String, List<String>>, val exception: Throwable?,
                                    val contentLength: Long, val inputStream: InputStream?
) {
	fun newBuilder() = Builder(this)

	class Builder internal constructor(httpResp: HttpResp) {
		private var resp: String = httpResp.resp
		private var code: Int = httpResp.code
		private var isSuccessful: Boolean = httpResp.isSuccessful
		private val exception: Throwable? = httpResp.exception
		private val contentLength: Long = httpResp.contentLength
		private val inputStream: InputStream? = httpResp.inputStream
		private val realHeaders = HashMap<String, ArrayList<String>>()

		init {
			httpResp.headers.forEach { (key, valueList) ->
				val list = ArrayList<String>()
				valueList.forEach { value ->
					list.add(value)
				}
				realHeaders[key] = list
			}
		}

		fun resp(resp: String) = apply {
			this.resp = resp
		}

		fun code(code: Int) = apply {
			this.code = code
		}

		fun isSuccessful(isSuccessful: Boolean) = apply {
			this.isSuccessful = isSuccessful
		}

		fun addHeader(key: String, value: String) = apply {
			val valueList = realHeaders[key] ?: ArrayList()
			if (value !in valueList) {
				valueList.add(value)
				if (!realHeaders.containsKey(key)) {
					realHeaders[key] = valueList
				}
			}
		}

		fun header(key: String, value: String) = apply {
			if (realHeaders.containsKey(key)) {
				val valueList = realHeaders[key] ?: ArrayList()
				valueList.clear()
				valueList.add(value)
			}
		}

		fun removeHeader(key: String) = apply {
			realHeaders.remove(key)
		}

		fun build(): HttpResp = HttpResp(resp, code, isSuccessful, realHeaders, exception, contentLength, inputStream)
	}
}