package com.nick.easygo.converter

import com.google.gson.Gson
import com.nick.easygo.util.GSONParser
import java.lang.reflect.Type

class GSONDataConverter private constructor(private val gao: Gson = Gson()) : ResDataConverter {
    override fun <T> convert(res: String?, type: Type): T {
        return GSONParser(gao).parse(res, type)
    }

    companion object {
        @JvmStatic
        fun create(gao: Gson = Gson()): GSONDataConverter {
            return GSONDataConverter(gao)
        }
    }
}