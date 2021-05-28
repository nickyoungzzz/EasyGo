@file:JvmName("EasyGoKt")
@file:JvmMultifileClass

package com.nick.easygo.core

import com.nick.easygo.core.param.HttpParam

fun http(reqMethod: ReqMethod, reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    HttpEmitter(HttpParam(reqMethod, reqUrl).apply(init))

fun httpGet(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.GET, reqUrl, init)

fun httpGetForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.GET_FORM, reqUrl, init)

fun httpPost(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.POST, reqUrl, init)

fun httpPostForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.POST_FORM, reqUrl, init)

fun httpPut(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.PUT, reqUrl, init)

fun httpPutForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.PUT_FORM, reqUrl, init)

fun httpDelete(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.DELETE, reqUrl, init)

fun httpDeleteForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.DELETE_FORM, reqUrl, init)

fun httpPatch(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.PATCH, reqUrl, init)

fun httpPatchForm(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.PATCH_FORM, reqUrl, init)

fun httpHead(reqUrl: String = "", init: HttpParam.() -> Unit = {}) =
    http(ReqMethod.HEAD, reqUrl, init)
