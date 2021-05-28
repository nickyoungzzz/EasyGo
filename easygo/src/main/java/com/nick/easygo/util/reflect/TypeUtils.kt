package com.nick.easygo.util.reflect

import java.lang.reflect.*
import java.lang.reflect.Array

fun getRawType(type: Type): Class<*> {
    return when (type) {
        is Class<*> -> type
        is ParameterizedType -> type.rawType as Class<*>
        is GenericArrayType -> Array.newInstance(getRawType(type.genericComponentType), 0).javaClass
        is TypeVariable<*> -> Any::class.java
        is WildcardType -> getRawType(type.upperBounds[0])
        else -> type.javaClass
    }
}

fun canonicalize(type: Type): Type {
    return when (type) {
        is Class<*> -> if (type.isArray) GenericArrayTypeImpl(canonicalize(type.componentType)) else type
        is ParameterizedType -> ParameterizedTypeImpl(
            type.ownerType,
            type.rawType,
            type.actualTypeArguments
        )
        is GenericArrayType -> GenericArrayTypeImpl(type.genericComponentType)
        is WildcardType -> WildcardTypeImpl(type.upperBounds, type.lowerBounds)
        else -> type
    }
}