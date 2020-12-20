package com.nick.easygo.parse

import java.lang.reflect.Type

interface ResDataConverter {
	fun <T> convert(res: String?, type: Type): T
}