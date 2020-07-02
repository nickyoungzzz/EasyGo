@file:JvmName("EasyHttpKt")
@file:JvmMultifileClass

package com.nick.easyhttp.core

import com.nick.easyhttp.core.param.HttpParam

fun http(reqMethod: ReqMethod, init: HttpParam.() -> Unit) = HttpSend(HttpParam(reqMethod).apply(init))

fun httpGet(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.GET).apply(init))

fun httpGetForm(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.GET_FORM).apply(init))

fun httpPost(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.POST).apply(init))

fun httpPostForm(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.POST_FORM).apply(init))

fun httpPut(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.PUT).apply(init))

fun httpPutForm(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.PUT_FORM).apply(init))

fun httpDelete(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.DELETE).apply(init))

fun httpDeleteForm(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.DELETE_FORM).apply(init))

fun httpPatch(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.PATCH).apply(init))

fun httpPatchForm(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.PATCH_FORM).apply(init))

fun httpHead(init: HttpParam.() -> Unit) = HttpSend(HttpParam(ReqMethod.HEAD).apply(init))
