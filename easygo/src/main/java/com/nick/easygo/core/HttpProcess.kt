@file:JvmName("EasyGoKt")
@file:JvmMultifileClass

package com.nick.easygo.core

import com.nick.easygo.core.param.HttpParam

fun http(reqMethod: ReqMethod, init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(reqMethod).apply(init))

fun httpGet(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.GET).apply(init))

fun httpGetForm(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.GET_FORM).apply(init))

fun httpPost(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.POST).apply(init))

fun httpPostForm(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.POST_FORM).apply(init))

fun httpPut(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.PUT).apply(init))

fun httpPutForm(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.PUT_FORM).apply(init))

fun httpDelete(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.DELETE).apply(init))

fun httpDeleteForm(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.DELETE_FORM).apply(init))

fun httpPatch(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.PATCH).apply(init))

fun httpPatchForm(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.PATCH_FORM).apply(init))

fun httpHead(init: HttpParam.() -> Unit) = HttpEmitter(HttpParam(ReqMethod.HEAD).apply(init))
