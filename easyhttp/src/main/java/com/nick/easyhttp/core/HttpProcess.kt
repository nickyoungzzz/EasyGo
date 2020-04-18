@file:JvmName("EasyHttp")
@file:JvmMultifileClass

package com.nick.easyhttp.core

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