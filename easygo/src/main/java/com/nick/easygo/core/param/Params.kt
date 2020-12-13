package com.nick.easygo.core.param

import com.nick.easygo.core.annotation.HttpDslMaker

private const val HEADER_COOKIE = "Cookie"
private const val CACHE_CONTROL = "Cache-Control"

@HttpDslMaker
internal interface ReqBody

open class Params : ReqBody {
	private val dataMap = hashMapOf<String, String>()
	infix fun String.with(value: String) {
		dataMap[this] = value
	}

	internal fun addTo(action: (String, String) -> Unit) = dataMap.forEach { (key, value) -> action(key, value) }
}

class Header : Params() {
	fun cookie(init: Cookie.() -> Unit) {
		HEADER_COOKIE with buildString {
			Cookie().apply(init).addTo { k, v ->
				append("$k=$v;")
			}
		}
	}

	fun cacheControl(init: CacheControl.() -> Unit) {
		CACHE_CONTROL with buildString {
			CacheControl().apply(init).addTo { k, v ->
				append("$k=$v;")
			}
		}
	}
}

class Query : Params()

class Field : Params()

class Json constructor(var json: String = "") : ReqBody

class Cookie : Params()

class CacheControl : Params()

class Part : ReqBody {
	private val dataMap = hashMapOf<String, Any>()
	infix fun String.with(value: Any) {
		dataMap[this] = value
	}

	internal fun addTo(action: (String, Any) -> Unit) = dataMap.forEach { (key, value) -> action(key, value) }
}

class Multi constructor(var multi: Boolean = false) : ReqBody

class Body : ReqBody {
	private val dataMapList = ArrayList<ReqBody>()

	fun field(init: Field.() -> Unit) {
		dataMapList.add(Field().apply(init))
	}

	fun json(json: String) {
		dataMapList.add(Json(json))
	}

	fun part(init: Part.() -> Unit) {
		dataMapList.add(Part().apply(init))
	}

	fun multi(multi: Boolean) {
		dataMapList.add(Multi(multi))
	}

	internal fun addTo(action: (ReqBody) -> Unit) = dataMapList.forEach { action(it) }
}
