package com.nick.easyhttp.result

import com.nick.easyhttp.core.ReqMethod

data class HttpReq internal constructor(var url: String, var reqMethod: ReqMethod, var reqTag: Any?,
                                        val httpReqHead: HttpReqHead, val httpReqBody: HttpReqBody,
                                        var asDownload: Boolean
)

data class HttpReqBody internal constructor(val fieldMap: HashMap<String, String>, val multipartBody: HashMap<String, Any>,
                                            var isMultiPart: Boolean, var jsonString: String
) {
	fun addOrReplaceField(key: String, value: String) {
		fieldMap[key] = value
	}

	fun removeField(key: String) {
		fieldMap.remove(key)
	}

	fun addOrReplacePart(key: String, value: Any) {
		multipartBody[key] = value
	}

	fun removePart(key: String) {
		multipartBody.remove(key)
	}
}

data class HttpReqHead internal constructor(val headerMap: HashMap<String, String>, val queryMap: HashMap<String, String>) {
	fun addOrReplaceHeader(key: String, value: String) {
		headerMap[key] = value
	}

	fun removeHeader(key: String) {
		headerMap.remove(key)
	}

	fun addOrReplaceQuery(key: String, value: String) {
		queryMap[key] = value
	}

	fun removeQuery(key: String) {
		queryMap.remove(key)
	}
}