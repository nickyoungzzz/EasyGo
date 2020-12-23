package com.nick.easygo.converter

import java.lang.reflect.Type

interface ResDataConverter {
	fun <T> convert(res: String?, type: Type): T
}