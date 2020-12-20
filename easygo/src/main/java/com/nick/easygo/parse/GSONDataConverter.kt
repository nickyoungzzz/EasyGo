package com.nick.easygo.parse

import com.google.gson.Gson
import com.nick.easygo.util.GSONParser
import java.lang.reflect.Type

class GSONDataConverter(private val gao: Gson = Gson()) : ResDataConverter {
	override fun <T> convert(res: String?, type: Type): T {
		return GSONParser(gao).parse(res, type)
	}
}