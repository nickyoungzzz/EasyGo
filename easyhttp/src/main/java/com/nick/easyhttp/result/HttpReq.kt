package com.nick.easyhttp.result

import com.nick.easyhttp.core.ReqMethod

class HttpReq internal constructor(val url: String, val reqMethod: ReqMethod, val reqTag: Any?,
                                   val headerMap: Map<String, String>, val queryMap: Map<String, String>,
                                   val httpReqBody: HttpReqBody, var asDownload: Boolean
) {
	fun newBuilder() = Builder(this)

	class Builder internal constructor(httpReq: HttpReq) {
		private var url: String = httpReq.url
		private var reqMethod: ReqMethod = httpReq.reqMethod
		private var reqTag: Any? = httpReq.reqTag
		private val realHeaderMap = HashMap<String, String>()
		private val realQueryMap = HashMap<String, String>()
		private var httpReqBody: HttpReqBody = httpReq.httpReqBody
		private var asDownload: Boolean = httpReq.asDownload

		init {
			httpReq.headerMap.forEach { (key, value) ->
				realHeaderMap[key] = value
			}
			httpReq.queryMap.forEach { (key, value) ->
				realQueryMap[key] = value
			}
		}

		fun url(url: String) = apply { this.url = url }

		fun reqMethod(reqMethod: ReqMethod) = apply { this.reqMethod = reqMethod }

		fun reqTag(reqTag: Any?) = apply { this.reqTag = reqTag }

		fun asDownload(asDownload: Boolean) = apply { this.asDownload = asDownload }

		fun httpReqBody(httpReqBody: HttpReqBody) = apply { this.httpReqBody = httpReqBody }

		fun addHeader(key: String, value: String) = apply {
			if (!realHeaderMap.containsKey(key)) {
				realHeaderMap[key] = value
			}
		}

		fun header(key: String, value: String) = apply {
			if (realHeaderMap.containsKey(key)) {
				realHeaderMap[key] = value
			}
		}

		fun removeHeader(key: String) = apply {
			realHeaderMap.remove(key)
		}

		fun addQuery(key: String, value: String) = apply {
			if (!realQueryMap.containsKey(key)) {
				realQueryMap[key] = value
			}
		}

		fun query(key: String, value: String) = apply {
			if (realQueryMap.containsKey(key)) {
				realQueryMap[key] = value
			}
		}

		fun removeQuery(key: String) = apply {
			realQueryMap.remove(key)
		}

		fun build() = HttpReq(url, reqMethod, reqTag, realHeaderMap, realQueryMap, httpReqBody, asDownload)
	}
}

class HttpReqBody internal constructor(val fieldMap: Map<String, String>, val multipartBody: Map<String, Any>,
                                       val isMultiPart: Boolean, val jsonString: String
) {
	fun newBuilder() = Builder(this)

	class Builder internal constructor(httpReqBody: HttpReqBody) {
		private val realFieldMap = HashMap<String, String>()
		private val realMultipartBody = HashMap<String, Any>()
		private var isMultiPart = httpReqBody.isMultiPart
		private var jsonString = httpReqBody.jsonString

		init {
			httpReqBody.fieldMap.forEach { (key, value) ->
				realFieldMap[key] = value
			}
			httpReqBody.multipartBody.forEach { (key, value) ->
				realMultipartBody[key] = value
			}
		}

		fun isMultiPart(isMultiPart: Boolean) = apply { this.isMultiPart = isMultiPart }

		fun jsonString(jsonString: String) = apply { this.jsonString = jsonString }

		fun addField(key: String, value: String) = apply {
			if (!realFieldMap.containsKey(key)) {
				realFieldMap[key] = value
			}
		}

		fun field(key: String, value: String) = apply {
			if (realFieldMap.containsKey(key)) {
				realFieldMap[key] = value
			}
		}

		fun removeField(key: String) = apply {
			realFieldMap.remove(key)
		}

		fun addPart(key: String, value: String) = apply {
			if (!realMultipartBody.containsKey(key)) {
				realMultipartBody[key] = value
			}
		}

		fun part(key: String, value: String) = apply {
			if (realMultipartBody.containsKey(key)) {
				realMultipartBody[key] = value
			}
		}

		fun removePart(key: String) = apply {
			realMultipartBody.remove(key)
		}

		fun build() = HttpReqBody(realFieldMap, realMultipartBody, isMultiPart, jsonString)
	}
}