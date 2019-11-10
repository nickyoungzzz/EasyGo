package com.nick.lib.network.interfaces

import com.nick.lib.network.HttpResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class HttpCallBack<T> {

	abstract fun onResult(httpResult: HttpResult<T>)

	fun getGenericType(index: Int): Type {
		val type = this.javaClass.genericSuperclass
		val typeArray = ((type as ParameterizedType).actualTypeArguments)
		return typeArray[index]
	}

}