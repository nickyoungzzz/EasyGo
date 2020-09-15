package com.nick.easygo.core.param

interface ReqBody

open class Params : ReqBody {
	private val dataMap = hashMapOf<String, String>()
	infix fun String.with(value: String) {
		dataMap[this] = value
	}

	internal fun addTo(action: (k: String, v: String) -> Unit) = dataMap.forEach { (key, value) -> action(key, value) }
}

class Header : Params()

class Query : Params()

class Field : Params()

class Json constructor(var json: String = "") : ReqBody

class Part : ReqBody {
	private val dataMap = hashMapOf<String, Any>()
	infix fun String.with(value: Any) {
		dataMap[this] = value
	}

	internal fun addTo(action: (k: String, v: Any) -> Unit) = dataMap.forEach { (key, value) -> action(key, value) }
}

class Multi constructor(var multi: Boolean = false) : ReqBody

class Body {
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

	internal fun addTo(action: (body: ReqBody) -> Unit) = dataMapList.forEach { reqBody -> action(reqBody) }
}
