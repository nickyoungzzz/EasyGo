@file:JvmName("EasyGoKt")
@file:JvmMultifileClass

package com.nick.easygo.core

import com.nick.easygo.core.param.HttpParam

fun http(reqMethod: ReqMethod, reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(reqMethod, reqUrl).apply(init))

fun httpGet(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.GET, reqUrl).apply(init))

fun httpGetForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.GET_FORM, reqUrl).apply(init))

fun httpPost(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.POST, reqUrl).apply(init))

fun httpPostForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.POST_FORM, reqUrl).apply(init))

fun httpPut(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.PUT, reqUrl).apply(init))

fun httpPutForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.PUT_FORM, reqUrl).apply(init))

fun httpDelete(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.DELETE, reqUrl).apply(init))

fun httpDeleteForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.DELETE_FORM, reqUrl).apply(init))

fun httpPatch(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.PATCH, reqUrl).apply(init))

fun httpPatchForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.PATCH_FORM, reqUrl).apply(init))

fun httpHead(reqUrl: String = "", init: HttpParam.() -> Unit = {}) = HttpEmitter(HttpParam(ReqMethod.HEAD, reqUrl).apply(init))
