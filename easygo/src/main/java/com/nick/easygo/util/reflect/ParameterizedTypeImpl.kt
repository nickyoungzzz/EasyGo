package com.nick.easygo.util.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(private val ownerType: Type, private val rawType: Type, private val typeArguments: Array<Type>) : ParameterizedType {

	override fun getActualTypeArguments(): Array<Type> = typeArguments

	override fun getRawType(): Type = rawType

	override fun getOwnerType(): Type = ownerType
}