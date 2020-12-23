package com.nick.easygo.util

import com.google.gson.Gson
import java.lang.reflect.Type

interface JsonStringParser {
	fun <T> parse(result: String?, type: Type): T
}

class GSONParser(private val gao: Gson = Gson()) : JsonStringParser {

	override fun <T> parse(result: String?, type: Type): T {
		return toAny(result, type)
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> toAny(result: String?, type: Type): T {
		return when (type) {
			String::class.java -> result as T
			Int::class.java -> result?.toInt() as T
			Long::class.java -> result?.toLong() as T
			Float::class.java -> result?.toFloat() as T
			Double::class.java -> result?.toDouble() as T
			Short::class.java -> result?.toShort() as T
			Byte::class.java -> result?.toByte() as T
			CharArray::class.java -> result?.toCharArray() as T
			Boolean::class.java -> result?.toBoolean() as T
			else -> gao.fromJson(result, type)
		}
	}
}