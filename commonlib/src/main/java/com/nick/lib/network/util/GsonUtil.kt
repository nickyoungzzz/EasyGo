@file:JvmName("GsonUtil")

package com.nick.lib.network.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

fun <T> String.parseAsObject(clazz: Class<T>): T = Gson().fromJson(this, clazz)

fun <T> String.parseAsList(clazz: Class<T>): MutableList<T> = Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)

fun Map<String, String>.toJsonString() = JSONObject(this).toString()

fun <T> objectToJsonString(t: T): String = Gson().toJson(t)
