@file:JvmName("EasyHttp")

package com.nick.lib.network

import com.nick.lib.network.interfaces.HttpConfig
import com.nick.lib.network.interfaces.HttpDelegate
import com.nick.lib.network.interfaces.ReqMethod

fun get(url: String) = HttpDelegate(url, ReqMethod.GET)

fun post(url: String): HttpDelegate = HttpDelegate(url, ReqMethod.POST)

fun getForm(url: String) = HttpDelegate(url, ReqMethod.GET_FORM)

fun postForm(url: String) = HttpDelegate(url, ReqMethod.POST_FORM)

fun put(url: String) = HttpDelegate(url, ReqMethod.PUT)

fun putForm(url: String) = HttpDelegate(url, ReqMethod.PUT_FORM)

fun delete(url: String) = HttpDelegate(url, ReqMethod.DELETE)

fun deleteForm(url: String) = HttpDelegate(url, ReqMethod.DELETE_FORM)

@Volatile var hasConfig = false

@Synchronized fun configEasyHttp(httpConfig: HttpConfig) {
	if (hasConfig) {
		throw RuntimeException("Do not config again")
	}
	val client = httpConfig.okHttpConfig(HttpProcessorFactory.okHttpClientBuilder)
	HttpProcessorFactory.retrofit = HttpProcessorFactory.retrofitBuilder.baseUrl(httpConfig.baseUrl()).client(client).build()
	hasConfig = true
}