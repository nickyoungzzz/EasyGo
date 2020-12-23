package com.nick.easygo.util.reflect

import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

class GenericArrayTypeImpl(private val genericComponentType: Type) : GenericArrayType {

	override fun getGenericComponentType(): Type = genericComponentType
}