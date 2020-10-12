package com.nick.easygo.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String?.toAny(block: (String?, Class<T>) -> T = GSONParser()::toAny): T {
	return when (T::class.java) {
		String::class.java -> this as T
		Int::class.java -> this?.toInt() as T
		Long::class.java -> this?.toLong() as T
		Float::class.java -> this?.toFloat() as T
		Double::class.java -> this?.toDouble() as T
		Short::class.java -> this?.toShort() as T
		Byte::class.java -> this?.toByte() as T
		CharArray::class.java -> this?.toCharArray() as T
		Boolean::class.java -> this?.toBoolean() as T
		else -> block.invoke(this, T::class.java)
	}
}

inline fun <reified T> String?.toAnyList(block: (String?, Class<T>) -> List<T> = GSONParser()::toAnyList): List<T> {
	return block.invoke(this, T::class.java)
}

interface JsonStringParser {
	fun <T> toAny(result: String?, clazz: Class<T>): T
	fun <T> toAnyList(result: String?, clazz: Class<T>): List<T>
}

class GSONParser(private val gao: Gson = Gson()) : JsonStringParser {

	override fun <T> toAny(result: String?, clazz: Class<T>): T {
		return gao.fromJson(result, clazz)
	}

	override fun <T> toAnyList(result: String?, clazz: Class<T>): List<T> {
		return gao.fromJson(result, object : TypeToken<List<T>>() {}.type)
	}
}