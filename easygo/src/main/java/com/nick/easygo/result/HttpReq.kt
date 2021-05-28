package com.nick.easygo.result

import com.nick.easygo.core.ReqMethod

class HttpReq internal constructor(
    val url: String, val reqMethod: ReqMethod, val reqTag: Any?,
    val headerMap: Map<String, String>, val queryMap: Map<String, String>,
    val httpReqBody: HttpReqBody, var asDownload: Boolean
) {
    fun newBuilder() = Builder(this)

    class Builder internal constructor(httpReq: HttpReq) {
        private var url: String = httpReq.url
        private var reqMethod: ReqMethod = httpReq.reqMethod
        private var reqTag: Any? = httpReq.reqTag
        private val realHeaderMap = HashMap<String, String>()
        private val realQueryMap = HashMap<String, String>()
        private var httpReqBody: HttpReqBody = httpReq.httpReqBody
        private var asDownload: Boolean = httpReq.asDownload

        init {
            httpReq.headerMap.forEach { (key, value) ->
                realHeaderMap[key] = value
            }
            httpReq.queryMap.forEach { (key, value) ->
                realQueryMap[key] = value
            }
        }

        fun url(url: String) = apply { this.url = url }

        fun reqMethod(reqMethod: ReqMethod) = apply { this.reqMethod = reqMethod }

        fun reqTag(reqTag: Any?) = apply { this.reqTag = reqTag }

        fun asDownload(asDownload: Boolean) = apply { this.asDownload = asDownload }

        fun httpReqBody(httpReqBody: HttpReqBody) = apply { this.httpReqBody = httpReqBody }

        fun addHeader(key: String, value: String) = apply {
            if (!realHeaderMap.containsKey(key)) {
                realHeaderMap[key] = value
            }
        }

        fun header(key: String, value: String) = apply {
            if (realHeaderMap.containsKey(key)) {
                realHeaderMap[key] = value
            }
        }

        fun removeHeader(key: String) = apply {
            realHeaderMap.remove(key)
        }

        fun addQuery(key: String, value: String) = apply {
            if (!realQueryMap.containsKey(key)) {
                realQueryMap[key] = value
            }
        }

        fun query(key: String, value: String) = apply {
            if (realQueryMap.containsKey(key)) {
                realQueryMap[key] = value
            }
        }

        fun removeQuery(key: String) = apply {
            realQueryMap.remove(key)
        }

        fun build() =
            HttpReq(url, reqMethod, reqTag, realHeaderMap, realQueryMap, httpReqBody, asDownload)
    }
}