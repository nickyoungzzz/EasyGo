package com.nick.easyhttp.core.param

import com.nick.easyhttp.core.ReqMethod

class HttpParam constructor(internal var reqMethod: ReqMethod) {

	internal var url: String = ""

	internal var queryMap = hashMapOf<String, String>()

	internal var headerMap = hashMapOf<String, String>()

	internal var fieldMap = hashMapOf<String, String>()

	internal val multipartBody = hashMapOf<String, Any>()

	internal var isMultiPart = false

	internal var jsonString = ""

	fun url(url: String) {
		this.url = url
	}

	fun head(init: Head.() -> Unit) {
		Head().apply(init).addTo { body ->
			when (body) {
				is Header -> body.addTo { k, v -> headerMap[k] = v }
				is Query -> body.addTo { k, v -> queryMap[k] = v }
			}
		}
	}

	fun body(init: Body.() -> Unit) {
		Body().apply(init).addTo { body ->
			when (body) {
				is Form -> body.addTo { k, v -> fieldMap[k] = v }
				is Json -> jsonString = body.json
				is Part -> body.addTo { k, v -> multipartBody[k] = v }
				is Multi -> isMultiPart = body.multi
			}
		}
	}
}