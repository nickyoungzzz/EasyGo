package com.nick.easyhttp.core.req.urlconnection

import com.nick.easyhttp.config.EasyHttp
import com.nick.easyhttp.core.ReqMethod
import com.nick.easyhttp.core.download.IDownloadHandler
import com.nick.easyhttp.core.download.OkIoDownHandler
import com.nick.easyhttp.core.req.IHttpHandler
import com.nick.easyhttp.core.req.okhttp.OkHttpHandler
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import com.nick.easyhttp.util.SslHelper
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class UrlConnectionClient constructor(builder: Builder) {

	var proxy = builder.proxy
	var httpHandler = builder.httpHandler
	var hostnameVerifier = builder.hostNameVerifier
	var sslSocketFactory = builder.sslSocketFactory
	var x509TrustManager = builder.x509TrustManager
	var downLoadHandler = builder.downloadHandler
	var connectTimeout = builder.connectTimeOut
	var readTimeOut = builder.readTimeOut
	var writeTimeOut = builder.writeTimeOut
	var interceptor = builder.interceptor

	constructor() : this(Builder())

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var proxy: Proxy = Proxy.NO_PROXY
		internal var httpHandler: IHttpHandler = OkHttpHandler()
		internal var hostNameVerifier: HostnameVerifier = SslHelper.getHostnameVerifier()
		internal var sslSocketFactory: SSLSocketFactory = SslHelper.getSSLSocketFactory()
		internal var x509TrustManager: X509TrustManager = SslHelper.getTrustManager()
		internal var downloadHandler: IDownloadHandler = OkIoDownHandler()
		internal var connectTimeOut: Long = 15000L
		internal var readTimeOut: Long = 15000L
		internal var writeTimeOut: Long = 15000L
		internal var interceptor = fun(_: HttpReq, httpResp: HttpResp) = httpResp

		constructor(urlConnectionClient: UrlConnectionClient) : this() {
			this.proxy = urlConnectionClient.proxy
			this.httpHandler = urlConnectionClient.httpHandler
			this.hostNameVerifier = urlConnectionClient.hostnameVerifier
			this.sslSocketFactory = urlConnectionClient.sslSocketFactory
			this.x509TrustManager = urlConnectionClient.x509TrustManager
			this.downloadHandler = urlConnectionClient.downLoadHandler
			this.connectTimeOut = urlConnectionClient.connectTimeout
			this.readTimeOut = urlConnectionClient.readTimeOut
			this.writeTimeOut = urlConnectionClient.writeTimeOut
			this.interceptor = urlConnectionClient.interceptor
		}

		fun proxy(proxy: Proxy) = apply { this.proxy = proxy }

		fun httpHandler(httpHandler: IHttpHandler) = apply { this.httpHandler = httpHandler }

		fun hostNameVerifier(hostNameVerifier: HostnameVerifier) = apply { this.hostNameVerifier = hostNameVerifier }

		fun sslSocketFactory(sslSocketFactory: SSLSocketFactory) = apply { this.sslSocketFactory = sslSocketFactory }

		fun x509TrustManager(x509TrustManager: X509TrustManager) = apply { this.x509TrustManager = x509TrustManager }

		fun downloadHandler(downloadHandler: IDownloadHandler) = apply { this.downloadHandler = downloadHandler }

		fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

		fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

		fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

		fun interceptor(interceptor: (httpReq: HttpReq, httpResp: HttpResp) -> HttpResp) = apply { this.interceptor = interceptor }

		fun build() = UrlConnectionClient(this)
	}

	private lateinit var connection: HttpsURLConnection

	fun execute(urlConnectionReq: UrlConnectionReq): UrlConnectionResp {
		if (urlConnectionReq.reqMethod in setOf(ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD)) {
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
		connection = url.openConnection(EasyHttp.urlConnectionClient.proxy) as HttpsURLConnection
		connection.requestMethod = urlConnectionReq.reqMethod.method
		connection.connectTimeout = EasyHttp.urlConnectionClient.connectTimeout.toInt()
		connection.readTimeout = EasyHttp.urlConnectionClient.readTimeOut.toInt()
		connection.doOutput = true
		connection.hostnameVerifier = EasyHttp.urlConnectionClient.hostnameVerifier
		connection.sslSocketFactory = connection.sslSocketFactory
		connection.addRequestProperty("accept-encoding", "gzip, deflate, br")
		when (urlConnectionReq.reqMethod) {
			ReqMethod.GET, ReqMethod.GET_FORM, ReqMethod.HEAD -> connection.doOutput = false
			ReqMethod.POST, ReqMethod.PUT, ReqMethod.DELETE, ReqMethod.PATCH -> {
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
					connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
					urlConnectionReq.multipartBody.forEach { (key, value) ->
						outputStream.writeBytes("$end$twoHyphens$boundary")
						outputStream.writeBytes("Content-Disposition: form-data; $key:${value.run {
							if (this is File) run {
								outputStream.write(FileInputStream(this as File).readBytes())
								this.absolutePath
							} else this
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
		val urlConnectionRespBuilder = UrlConnectionResp.Builder()
		try {
			connection.connect()
			val success = connection.responseCode in HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE
			val inputStream = if (success) connection.inputStream else connection.errorStream
			val resp = if (urlConnectionReq.asDownload) "" else inputStream.readBytes().toString(Charset.defaultCharset())
			urlConnectionRespBuilder.code(connection.responseCode)
				.byteData(inputStream)
				.isSuccessful(success)
				.headers(connection.headerFields)
				.contentLength(connection.contentLength.toLong())
				.resp(resp)
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