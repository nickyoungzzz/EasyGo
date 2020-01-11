@file:JvmName("GsonUtil")

package com.nick.easyhttp.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.json.JSONObject

fun <T> String.parseAsObject(clazz: Class<T>): T = GsonObject.create().fromJson(this, clazz)

fun <T> String.parseAsList(clazz: Class<T>): MutableList<T> {
	val list = arrayListOf<T>()
	val jsonAny = JsonParser.parseString(this).asJsonArray
	jsonAny.forEach { obj ->
		list.add(GsonObject.create().fromJson(obj, clazz))
	}
	return list
}

fun Map<String, String>.toJsonString() = JSONObject(this).toString()

fun <T> objectToJsonString(t: T): String = GsonObject.create().toJson(t)

internal object GsonObject {
	private lateinit var gao: Gson
	fun create(): Gson {
		if (!GsonObject::gao.isInitialized) {
			gao = GsonBuilder().serializeNulls().create()
		}
		return gao
	}
}
