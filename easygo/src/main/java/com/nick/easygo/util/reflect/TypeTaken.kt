package com.nick.easygo.util.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class TypeTaken<T> protected constructor() {
    var type: Type
        private set
    private var rawType: Type

    constructor(type: Type) : this() {
        this.type = canonicalize(type)
        this.rawType = getRawType(this.type)
    }

    init {
        this.type = getSuperclassTypeParameter(this::class.java)
        this.rawType = getRawType(this.type)
    }

    private fun getSuperclassTypeParameter(subclass: Class<*>): Type {
        val superclass = subclass.genericSuperclass
        if (superclass is Class<*>) {
            throw RuntimeException("Missing type parameter.")
        }
        val parameterized = superclass as ParameterizedType
        return canonicalize(parameterized.actualTypeArguments[0])
    }
}