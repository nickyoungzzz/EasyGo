package com.nick.easygo.result

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> HttpRawResult.toAny(crossinline result: (String?) -> T) = mapResult<T, HttpError> {
	result {
		result.invoke(it)
	}
}

inline fun <reified T> HttpRawResult.toAnyObject(noinline result: (String?) -> String?) = toAny {
	val invoke = result.invoke(it)
	when (val clazz = T::class.java) {
		String::class.java -> result as T
		Int::class.java -> invoke?.toInt() as T
		Long::class.java -> invoke?.toLong() as T
		Float::class.java -> invoke?.toFloat() as T
		Double::class.java -> invoke?.toDouble() as T
		Short::class.java -> invoke?.toShort() as T
		Byte::class.java -> invoke?.toByte() as T
		CharArray::class.java -> invoke?.toCharArray() as T
		Boolean::class.java -> invoke?.toBoolean() as T
		else -> this.httpResultParser.toAnyObject(invoke, clazz)
	}
}

inline fun <reified T> HttpRawResult.toAnyList(crossinline result: (String?) -> String?) = toAny {
	this.httpResultParser.toAnyList(result.invoke(it), T::class.java)
}

interface HttpResultParser {
	fun <T> toAnyObject(result: String?, clazz: Class<T>): T
	fun <T> toAnyList(result: String?, clazz: Class<T>): List<T>
}

class GSONResultParser : HttpResultParser {

	override fun <T> toAnyObject(result: String?, clazz: Class<T>): T {
		return Gson().fromJson(result, clazz)
	}

	override fun <T> toAnyList(result: String?, clazz: Class<T>): List<T> {
		return Gson().fromJson(result, object : TypeToken<List<T>>() {}.type)
	}
}