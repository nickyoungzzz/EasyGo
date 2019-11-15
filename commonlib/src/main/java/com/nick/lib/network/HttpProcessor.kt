@file:JvmName("EasyHttp")

package com.nick.lib.network

import com.nick.lib.network.interfaces.HttpConfig
import com.nick.lib.network.interfaces.HttpRequest
import com.nick.lib.network.interfaces.ReqMethod

fun String.get() = HttpRequest(this, ReqMethod.GET)

fun String.getForm(url: String) = HttpRequest(this, ReqMethod.GET_FORM)

fun String.post(): HttpRequest = HttpRequest(this, ReqMethod.POST)

fun String.postForm() = HttpRequest(this, ReqMethod.POST_FORM)

fun String.put() = HttpRequest(this, ReqMethod.PUT)

fun String.putForm() = HttpRequest(this, ReqMethod.PUT_FORM)

fun String.delete() = HttpRequest(this, ReqMethod.DELETE)

fun String.deleteForm() = HttpRequest(this, ReqMethod.DELETE_FORM)

@Volatile private var hasConfig = false

@Synchronized fun configEasyHttp(httpConfig: HttpConfig) {
	if (hasConfig) {
		throw RuntimeException("Do not config again")
	}
	val client = httpConfig.okHttpConfig(HttpConfigFactory.okHttpClientBuilder)
	HttpConfigFactory.retrofit = HttpConfigFactory.retrofitBuilder.baseUrl(httpConfig.baseUrl()).client(client).build()
	hasConfig = true
}