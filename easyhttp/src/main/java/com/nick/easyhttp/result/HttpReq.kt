package com.nick.easyhttp.result

import com.nick.easyhttp.enums.ReqMethod

class HttpReq internal constructor(builder: Builder) {

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

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var url = ""
		internal var reqMethod = ReqMethod.POST
		internal var reqTag: Any? = null
		internal var queryMap = hashMapOf<String, String>()
		internal var headerMap = hashMapOf<String, String>()
		internal var fieldMap = hashMapOf<String, String>()
		internal var multipartBody = hashMapOf<String, Any>()
		internal var isMultiPart = false
		internal var jsonString = "{}"
		internal var asDownload = false

		internal constructor(httpReq: HttpReq) : this() {
			this.url = httpReq.url
			this.reqMethod = httpReq.reqMethod
			this.reqTag = httpReq.reqTag
			this.queryMap = httpReq.queryMap
			this.headerMap = httpReq.headerMap
			this.fieldMap = httpReq.fieldMap
			this.multipartBody = httpReq.multipartBody
			this.isMultiPart = httpReq.isMultiPart
			this.jsonString = httpReq.jsonString
			this.asDownload = httpReq.asDownload
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

		fun build() = HttpReq(this)
	}
}