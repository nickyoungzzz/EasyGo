@file:JvmName("EasyHttp")

package com.nick.easyhttp.core

import com.nick.easyhttp.config.HttpHandlerConfig

fun String.get() = HttpRequest(this, ReqMethod.GET)

fun String.getForm(url: String) = HttpRequest(this, ReqMethod.GET_FORM)

fun String.post(): HttpRequest = HttpRequest(this, ReqMethod.POST)

fun String.postForm() = HttpRequest(this, ReqMethod.POST_FORM)

fun String.put() = HttpRequest(this, ReqMethod.PUT)

fun String.putForm() = HttpRequest(this, ReqMethod.PUT_FORM)

fun String.delete() = HttpRequest(this, ReqMethod.DELETE)

fun String.deleteForm() = HttpRequest(this, ReqMethod.DELETE_FORM)

fun String.patch() = HttpRequest(this, ReqMethod.PATCH)

fun String.patchForm() = HttpRequest(this, ReqMethod.PATCH_FORM)

fun String.head() = HttpRequest(this, ReqMethod.HEAD)

@Volatile private var hasConfig = false

lateinit var httpHandlerConfig: HttpHandlerConfig

@Synchronized fun init(handlerConfig: HttpHandlerConfig) {
	if (hasConfig) {
		throw RuntimeException("Do not config again")
	}
	httpHandlerConfig = handlerConfig
	hasConfig = true
}

enum class ReqMethod constructor(var method: String, var form: Boolean) {
	GET("GET", false), POST("POST", false), PUT("PUT", false), DELETE("DELETE", false),
	PATCH("PATCH", false), HEAD("HEAD", false), GET_FORM("GET", true), POST_FORM("POST", true),
	PUT_FORM("PUT", true), DELETE_FORM("DELETE", true), PATCH_FORM("PATCH", true)
}