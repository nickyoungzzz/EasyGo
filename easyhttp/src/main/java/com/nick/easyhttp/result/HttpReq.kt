package com.nick.easyhttp.result

import android.content.ContentQueryMap
import com.nick.easyhttp.enums.ReqMethod
import okhttp3.MultipartBody

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

		fun build() = HttpReq(this)
	}
}