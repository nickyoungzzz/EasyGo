package com.nick.easyhttp.core.req.urlconnection

import com.nick.easyhttp.core.ReqMethod

class UrlConnectionReq constructor(builder: Builder) {

	var url = builder.url
	var reqMethod = builder.reqMethod
	var reqTag: Any? = builder.reqTag
	var queryMap = builder.queryMap
	var headerMap = builder.headerMap
	var fieldMap = builder.fieldMap
	var multipartBody = builder.multipartBody
	var isMultiPart = builder.isMultiPart
	var jsonString = builder.jsonString
	var asDownload = builder.asDownload
	var connectTimeout = builder.connectTimeOut
	var readTimeOut = builder.readTimeOut
	var writeTimeOut = builder.writeTimeOut

	fun newBuilder() = Builder(this)

	companion object {
		private const val TIMEOUT = 15000L
	}

	class Builder constructor() {

		internal var url = ""
		internal var reqMethod = ReqMethod.POST
		internal var reqTag: Any? = null
		internal var queryMap = hashMapOf<String, String>()
		internal var headerMap = hashMapOf<String, String>()
		internal var fieldMap = hashMapOf<String, String>()
		internal var multipartBody = hashMapOf<String, Any>()
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

		fun queryMap(queryMap: HashMap<String, String>) = apply { this.queryMap = queryMap }

		fun headerMap(headerMap: HashMap<String, String>) = apply { this.headerMap = headerMap }

		fun fieldMap(fieldMap: HashMap<String, String>) = apply { this.fieldMap = fieldMap }

		fun multipartBody(multipartBody: HashMap<String, Any>) = apply { this.multipartBody = multipartBody }

		fun isMultiPart(isMultipart: Boolean) = apply { this.isMultiPart = isMultipart }

		fun jsonString(jsonString: String) = apply { this.jsonString = jsonString }

		fun asDownload(asDownload: Boolean) = apply { this.asDownload = asDownload }

		fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

		fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

		fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

		fun build() = UrlConnectionReq(this)
	}
}