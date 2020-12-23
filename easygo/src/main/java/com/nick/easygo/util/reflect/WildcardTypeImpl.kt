package com.nick.easygo.util.reflect

import java.lang.reflect.Type
import java.lang.reflect.WildcardType

class WildcardTypeImpl(private val upperBounds: Array<Type>, private val lowerBounds: Array<Type>) : WildcardType {

	override fun getUpperBounds(): Array<Type> = upperBounds

	override fun getLowerBounds(): Array<Type> = lowerBounds
}