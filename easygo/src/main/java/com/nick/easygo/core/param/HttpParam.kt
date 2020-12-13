package com.nick.easygo.core.param

import com.nick.easygo.core.ReqMethod
import com.nick.easygo.core.annotation.HttpDslMaker

@HttpDslMaker
class HttpParam constructor(internal val reqMethod: ReqMethod, reqUrl: String) {

	internal var url: String = reqUrl

	internal val queryMap = hashMapOf<String, String>()

	internal val headerMap = hashMapOf<String, String>()

	internal val fieldMap = hashMapOf<String, String>()

	internal val multipartBody = hashMapOf<String, Any>()

	internal var isMultiPart = false

	internal var jsonString = ""

	fun url(url: String) {
		this.url = url
	}

	fun header(init: Header.() -> Unit) {
		Header().apply(init).addTo { k, v -> headerMap[k] = v }
	}

	fun query(init: Query.() -> Unit) {
		Query().apply(init).addTo { k, v -> queryMap[k] = v }
	}

	fun body(init: Body.() -> Unit) {
		Body().apply(init).addTo {
			when (it) {
				is Field -> it.addTo { k, v -> fieldMap[k] = v }
				is Json -> jsonString = it.json
				is Part -> it.addTo { k, v -> multipartBody[k] = v }
				is Multi -> isMultiPart = it.multi
			}
		}
	}
}