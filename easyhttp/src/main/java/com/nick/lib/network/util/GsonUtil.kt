@file:JvmName("GsonUtil")

package com.nick.lib.network.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONObject

fun <T> String.parseAsObject(clazz: Class<T>): T = Gson().fromJson(this, clazz)

fun <T> String.parseAsList(clazz: Class<T>): MutableList<T> {
	val list = arrayListOf<T>()
	val jsonAny = JsonParser.parseString(this).asJsonArray
	val gao = Gson()
	jsonAny.forEach { obj ->
		list.add(gao.fromJson(obj, clazz))
	}
	return list
}

fun Map<String, String>.toJsonString() = JSONObject(this).toString()

fun <T> objectToJsonString(t: T): String = Gson().toJson(t)
