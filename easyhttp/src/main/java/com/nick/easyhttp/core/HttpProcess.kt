@file:JvmName("EasyHttpKt")
@file:JvmMultifileClass

package com.nick.easyhttp.core

fun String.get() = HttpSend(this, ReqMethod.GET)

fun String.getForm(url: String) = HttpSend(this, ReqMethod.GET_FORM)

fun String.post(): HttpSend = HttpSend(this, ReqMethod.POST)

fun String.postForm() = HttpSend(this, ReqMethod.POST_FORM)

fun String.put() = HttpSend(this, ReqMethod.PUT)

fun String.putForm() = HttpSend(this, ReqMethod.PUT_FORM)

fun String.delete() = HttpSend(this, ReqMethod.DELETE)

fun String.deleteForm() = HttpSend(this, ReqMethod.DELETE_FORM)

fun String.patch() = HttpSend(this, ReqMethod.PATCH)

fun String.patchForm() = HttpSend(this, ReqMethod.PATCH_FORM)

fun String.head() = HttpSend(this, ReqMethod.HEAD)