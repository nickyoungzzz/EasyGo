package com.nick.lib.network.interfaces

import com.nick.lib.network.HttpResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class HttpCallBack<T, F> {

	abstract fun onResult(httpResult: HttpResult<T, F>)

	abstract fun onLoading()

	fun getGenericType(index: Int): Type {
		val type = this.javaClass.genericSuperclass
		val typeArray = ((type as ParameterizedType).actualTypeArguments)
		if (typeArray.size < 2) {
			throw RuntimeException("no type defined")
		}
		return typeArray[index]
	}

}