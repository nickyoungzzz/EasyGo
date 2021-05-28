package com.nick.easygo.core.req.urlconnection

import com.nick.easygo.config.EasyGo
import com.nick.easygo.core.req.HttpHandler
import com.nick.easygo.result.HttpReq
import com.nick.easygo.result.HttpResp

class UrlConnectionHandler : HttpHandler {

    override fun execute(httpReq: HttpReq): HttpResp {
        val urlConnectionReq = UrlConnectionReq.Builder()
            .reqMethod(httpReq.reqMethod).reqTag(httpReq.reqTag).url(httpReq.url)
            .asDownload(httpReq.asDownload).isMultiPart(httpReq.httpReqBody.isMultiPart)
            .jsonString(httpReq.httpReqBody.jsonString).fieldMap(httpReq.httpReqBody.fieldMap)
            .headerMap(httpReq.headerMap).queryMap(httpReq.queryMap)
            .multipartBody(httpReq.httpReqBody.multipartBody).build()

        val urlConnectionResp = EasyGo.urlConnectionClient.proceed(urlConnectionReq)
        return HttpResp(
            urlConnectionResp.resp,
            urlConnectionResp.code,
            urlConnectionResp.isSuccessful,
            urlConnectionResp.headers,
            urlConnectionResp.exception,
            urlConnectionResp.contentLength,
            urlConnectionResp.inputStream,
            urlConnectionResp.url
        )
    }

    override fun cancel() {
        EasyGo.urlConnectionClient.cancel()
    }

    override val requestClient: String
        get() = "UrlConnection"
}