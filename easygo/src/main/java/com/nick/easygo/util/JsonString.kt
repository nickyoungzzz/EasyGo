package com.nick.easygo.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String?.toAny(httpResultParser: HttpResultParser = GSONResultParser()): T? {
	return when (val clazz = T::class.java) {
		String::class.java -> this as? T
		Int::class.java -> this?.toInt() as? T
		Long::class.java -> this?.toLong() as? T
		Float::class.java -> this?.toFloat() as? T
		Double::class.java -> this?.toDouble() as? T
		Short::class.java -> this?.toShort() as? T
		Byte::class.java -> this?.toByte() as? T
		CharArray::class.java -> this?.toCharArray() as? T
		Boolean::class.java -> this?.toBoolean() as? T
		else -> httpResultParser.toAnyObject(this, clazz)
	}
}

inline fun <reified T> String?.toAnyList(httpResultParser: HttpResultParser = GSONResultParser()): List<T> {
	return httpResultParser.toAnyList(this, T::class.java)
}

interface HttpResultParser {
	fun <T> toAnyObject(result: String?, clazz: Class<T>): T
	fun <T> toAnyList(result: String?, clazz: Class<T>): List<T>
}

class GSONResultParser : HttpResultParser {

	private val gao = Gson()
	override fun <T> toAnyObject(result: String?, clazz: Class<T>): T {
		return gao.fromJson(result, clazz)
	}

	override fun <T> toAnyList(result: String?, clazz: Class<T>): List<T> {
		return gao.fromJson(result, object : TypeToken<List<T>>() {}.type)
	}
}