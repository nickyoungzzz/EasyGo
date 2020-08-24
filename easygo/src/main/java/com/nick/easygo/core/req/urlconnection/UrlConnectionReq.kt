package com.nick.easygo.core.req.urlconnection

import com.nick.easygo.core.ReqMethod

class UrlConnectionReq constructor(builder: Builder) {

	var url = builder.url
	var reqMethod = builder.reqMethod
	var reqTag: Any? = builder.reqTag
	var queryMap: HashMap<String, String> = hashMapOf()
	var headerMap: HashMap<String, String> = hashMapOf()
	var fieldMap: HashMap<String, String> = hashMapOf()
	var multipartBody: HashMap<String, Any> = hashMapOf()
	var isMultiPart = builder.isMultiPart
	var jsonString = builder.jsonString
	var asDownload = builder.asDownload
	var connectTimeout = builder.connectTimeOut
	var readTimeOut = builder.readTimeOut
	var writeTimeOut = builder.writeTimeOut

	init {
		builder.queryMap.forEach { (key, value) -> queryMap[key] = value }
		builder.headerMap.forEach { (key, value) -> headerMap[key] = value }
		builder.fieldMap.forEach { (key, value) -> fieldMap[key] = value }
		builder.multipartBody.forEach { (key, value) -> multipartBody[key] = value }
	}

	fun newBuilder() = Builder(this)

	companion object {
		private const val TIMEOUT = 15000L
	}

	class Builder constructor() {

		internal var url = ""
		internal var reqMethod = ReqMethod.POST
		internal var reqTag: Any? = null
		internal var queryMap: Map<String, String> = hashMapOf()
		internal var headerMap: Map<String, String> = hashMapOf()
		internal var fieldMap: Map<String, String> = hashMapOf()
		internal var multipartBody: Map<String, Any> = hashMapOf()
		internal var isMultiPart = false
		internal var jsonString = ""
		internal var asDownload = false
		internal var connectTimeOut: Long = TIMEOUT
		internal var readTimeOut: Long = TIMEOUT
		internal var writeTimeOut: Long = TIMEOUT

		internal constructor(urlConnectionReq: UrlConnectionReq) : this() {
			this.url = urlConnectionReq.url
			this.reqMethod = urlConnectionReq.reqMethod
			this.reqTag = urlConnectionReq.reqTag
			this.queryMap = urlConnectionReq.queryMap
			this.headerMap = urlConnectionReq.headerMap
			this.fieldMap = urlConnectionReq.fieldMap
			this.multipartBody = urlConnectionReq.multipartBody
			this.isMultiPart = urlConnectionReq.isMultiPart
			this.jsonString = urlConnectionReq.jsonString
			this.asDownload = urlConnectionReq.asDownload
			this.connectTimeOut = urlConnectionReq.connectTimeout
			this.readTimeOut = urlConnectionReq.readTimeOut
			this.writeTimeOut = urlConnectionReq.writeTimeOut
		}

		fun url(url: String) = apply { this.url = url }

		fun reqMethod(reqMethod: ReqMethod) = apply { this.reqMethod = reqMethod }

		fun reqTag(reqTag: Any?) = apply { this.reqTag = reqTag }

		fun queryMap(queryMap: Map<String, String>) = apply { this.queryMap = queryMap }

		fun headerMap(headerMap: Map<String, String>) = apply { this.headerMap = headerMap }

		fun fieldMap(fieldMap: Map<String, String>) = apply { this.fieldMap = fieldMap }

		fun multipartBody(multipartBody: Map<String, Any>) = apply { this.multipartBody = multipartBody }

		fun isMultiPart(isMultipart: Boolean) = apply { this.isMultiPart = isMultipart }

		fun jsonString(jsonString: String) = apply { this.jsonString = jsonString }

		fun asDownload(asDownload: Boolean) = apply { this.asDownload = asDownload }

		fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

		fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

		fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

		fun build() = UrlConnectionReq(this)
	}
}