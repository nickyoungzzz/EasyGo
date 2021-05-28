package com.nick.easygo.core.req.urlconnection

import com.nick.easygo.core.ReqMethod
import com.nick.easygo.util.SslHelper
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.Proxy
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

internal class UrlConnectionClient constructor(builder: Builder) {

    var proxy = builder.proxy
    var hostnameVerifier = builder.hostNameVerifier
    var sslSocketFactory = builder.sslSocketFactory
    var x509TrustManager = builder.x509TrustManager
    var connectTimeout = builder.connectTimeOut
    var readTimeOut = builder.readTimeOut
    var writeTimeOut = builder.writeTimeOut
    var dns = builder.dns
    var interceptor = fun(urlConnectionReq: UrlConnectionReq): UrlConnectionResp {
        return proceedInternal(
            urlConnectionReq.newBuilder()
                .readTimeOut(this.readTimeOut)
                .writeTimeOut(this.readTimeOut)
                .connectTimeOut(this.connectTimeout)
                .build()
        )
    }

    constructor() : this(Builder())

    fun newBuilder() = Builder(this)

    companion object {
        private const val TIMEOUT = 15000L
    }

    class Builder constructor() {

        internal var proxy: Proxy = Proxy.NO_PROXY
        internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
        internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
        internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
        internal var connectTimeOut: Long = TIMEOUT
        internal var readTimeOut: Long = TIMEOUT
        internal var writeTimeOut: Long = TIMEOUT
        internal var dns = fun(host: String): Array<InetAddress> = InetAddress.getAllByName(host)

        constructor(urlConnectionClient: UrlConnectionClient) : this() {
            this.proxy = urlConnectionClient.proxy
            this.hostNameVerifier = urlConnectionClient.hostnameVerifier
            this.sslSocketFactory = urlConnectionClient.sslSocketFactory
            this.x509TrustManager = urlConnectionClient.x509TrustManager
            this.connectTimeOut = urlConnectionClient.connectTimeout
            this.readTimeOut = urlConnectionClient.readTimeOut
            this.writeTimeOut = urlConnectionClient.writeTimeOut
            this.dns = urlConnectionClient.dns
        }

        fun proxy(proxy: Proxy) = apply { this.proxy = proxy }

        fun hostNameVerifier(hostNameVerifier: HostnameVerifier) =
            apply { this.hostNameVerifier = hostNameVerifier }

        fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) =
            apply { this.sslSocketFactory = sslSocketFactory }

        fun x509TrustManager(x509TrustManager: X509TrustManager) =
            apply { this.x509TrustManager = x509TrustManager }

        fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

        fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

        fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

        fun dns(dns: (host: String) -> Array<InetAddress>) = apply { this.dns = dns }

        fun build() = UrlConnectionClient(this)
    }

    fun setInterceptor(interceptor: (urlConnectionReq: UrlConnectionReq) -> UrlConnectionResp) =
        apply {
            this.interceptor = interceptor
        }

    private fun makeDns(host: String, array: Array<InetAddress>) {
        val netAddressClass: Class<InetAddress> = InetAddress::class.java
        try {
            val field = netAddressClass.getDeclaredField("addressCache")
            field.isAccessible = true
            val obj = field[netAddressClass]
            val cacheClass = obj.javaClass
            val putMethod: Method = cacheClass.getDeclaredMethod(
                "put",
                String::class.java,
                Array<InetAddress>::class.java
            )
            putMethod.isAccessible = true
            putMethod.invoke(obj, host, array)
        } catch (e: NoSuchFieldException) {
        }
    }

    private lateinit var connection: HttpsURLConnection

    fun proceed(urlConnectionReq: UrlConnectionReq): UrlConnectionResp {
        return interceptor(urlConnectionReq)
    }

    internal fun proceedInternal(urlConnectionReq: UrlConnectionReq): UrlConnectionResp {
        if (urlConnectionReq.reqMethod in setOf(ReqMethod.GET_FORM, ReqMethod.HEAD)) {
            urlConnectionReq.fieldMap.forEach { (key, value) ->
                urlConnectionReq.queryMap[key] = value
            }
        }
        val regex = if (urlConnectionReq.url.contains("?")) "&" else "?"
        val stringBuilder = StringBuilder(if (urlConnectionReq.queryMap.isNotEmpty()) regex else "")
        urlConnectionReq.queryMap.forEach { (key, value) ->
            stringBuilder.append("$key=$value&")
        }
        val url = URL("${urlConnectionReq.url}${stringBuilder.toString().substringBeforeLast("&")}")
        stringBuilder.clear()
        val urlConnectionRespBuilder = UrlConnectionResp.Builder()
        try {
            connection = url.openConnection(this.proxy) as HttpsURLConnection
        } catch (e: IOException) {
            return urlConnectionRespBuilder.exception(e).build()
        }
        connection.requestMethod = urlConnectionReq.reqMethod.method
        connection.connectTimeout = urlConnectionReq.connectTimeout.toInt()
        connection.readTimeout = urlConnectionReq.readTimeOut.toInt()
        connection.doOutput = true
        connection.hostnameVerifier = this.hostnameVerifier
        connection.sslSocketFactory = connection.sslSocketFactory
        urlConnectionReq.headerMap.forEach { (key, value) ->
            connection.addRequestProperty(key, value)
        }
        when (urlConnectionReq.reqMethod) {
            ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD -> connection.doOutput = false
            ReqMethod.POST, ReqMethod.PUT, ReqMethod.DELETE, ReqMethod.PATCH -> {
                urlConnectionReq.headerMap["Content-Type"] = "application/json"
                connection.setRequestProperty("Content-Type", "application/json")
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(urlConnectionReq.jsonString)
                outputStream.flush()
                outputStream.close()
            }
            ReqMethod.POST_FORM -> {
                val outputStream = DataOutputStream(connection.outputStream)
                if (urlConnectionReq.isMultiPart) {
                    val end = "/r/n"
                    val twoHyphens = "--"
                    val boundary = "*****"

                    connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data;boundary=$boundary"
                    )
                    urlConnectionReq.multipartBody.forEach { (key, value) ->
                        outputStream.writeBytes("$end$twoHyphens$boundary")
                        outputStream.writeBytes("Content-Disposition: form-data; $key:${
                            value.run {
                                if (this is File) {
                                    val fileInputStream = FileInputStream(this)
                                    outputStream.write(fileInputStream.readBytes())
                                    fileInputStream.close()
                                    this.absolutePath
                                } else this@run
                            }
                        };$end")
                        outputStream.writeBytes("$end$twoHyphens$boundary")
                    }
                } else {
                    urlConnectionReq.fieldMap.forEach { (key, value) ->
                        stringBuilder.append("$key=$value&")
                    }
                    outputStream.writeBytes(stringBuilder.toString().substringBeforeLast("&"))
                }
                outputStream.flush()
                outputStream.close()
            }
            ReqMethod.PUT_FORM, ReqMethod.DELETE_FORM, ReqMethod.PATCH_FORM -> {
                throw RuntimeException("put, delete, patch method do not have multipart body or form body")
            }
        }
        try {
            makeDns(url.host, this.dns(url.host))
            connection.connect()
            val success =
                connection.responseCode in HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE
            val inputStream = if (success) connection.inputStream else connection.errorStream
            val resp = if (urlConnectionReq.asDownload) "" else inputStream.readBytes()
                .toString(Charset.defaultCharset())
            urlConnectionRespBuilder.code(connection.responseCode)
                .byteData(inputStream)
                .isSuccessful(success)
                .headers(connection.headerFields)
                .contentLength(connection.contentLength.toLong())
                .resp(resp)
                .url(connection.url.toString())
            if (!urlConnectionReq.asDownload) {
                inputStream.close()
            }
        } catch (e: IOException) {
            urlConnectionRespBuilder.exception(e)
        } finally {
            connection.disconnect()
        }
        return urlConnectionRespBuilder.build()
    }

    fun cancel() {
        connection.disconnect()
    }
}