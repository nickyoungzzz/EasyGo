package com.nick.easyhttp.core

import com.nick.easyhttp.enums.ReqMethod
import com.nick.easyhttp.result.HttpReq
import com.nick.easyhttp.result.HttpResp
import com.nick.easyhttp.result.HttpResult
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

class HttpRequest internal constructor(private val reqUrl: String, private val reqMethod: ReqMethod) {

	private var reqTag: Any? = null

	private var queryMap = hashMapOf<String, String>()

	private var headerMap = hashMapOf<String, String>()

	private var fieldMap = hashMapOf<String, String>()

	private val multipartBody = hashMapOf<String, Any>()

	private var isMultiPart = false

	private var jsonString = ""

	private var httpHandler: IHttpHandler = RetrofitHttpHandler()

	fun addQuery(key: String, value: String) = apply {
		queryMap[key] = value
	}

	fun addQueries(queryMap: HashMap<String, String>) = apply {
		if (queryMap.isNotEmpty()) {
			this.queryMap.forEach {
				if (!this.queryMap.containsKey(it.key)) {
					this.queryMap[it.key] = it.value
				}
			}
		}
	}

	fun addField(key: String, value: String) = apply {
		isMultiPart = false
		fieldMap[key] = value
	}

	fun addFields(fieldMap: HashMap<String, String>) = apply {
		if (fieldMap.isNotEmpty()) {
			fieldMap.forEach {
				if (!this.fieldMap.containsKey(it.key)) {
					addField(it.key, it.value)
				}
			}
		}
	}

	fun addJsonString(jsonString: String) = apply {
		this.jsonString = jsonString
	}

	fun addMultiPart(key: String, value: Any) = apply {
		isMultiPart = true
		multipartBody[key] = value
	}

	fun addHeader(key: String, value: String) = apply {
		headerMap[key] = value
	}

	fun addHeaders(headerMap: HashMap<String, String>) = apply {
		if (headerMap.isNotEmpty()) {
			headerMap.forEach {
				if (!this.headerMap.containsKey(it.key)) {
					addHeader(it.key, it.value)
				}
			}
		}
	}

	fun tag(reqTag: Any) = apply {
		this.reqTag = reqTag
	}

	fun isMultiPart(multiPart: Boolean) = apply {
		this.isMultiPart = multiPart
	}

	fun setHttpHandler(httpHandler: IHttpHandler) = apply {
		this.httpHandler = httpHandler
	}

	fun setHttpInterceptor(httpHandlerInterceptor: HttpInterceptor) = apply {
		this.httpHandler = Proxy.newProxyInstance(httpHandler.javaClass.classLoader, httpHandler.javaClass.interfaces,
			HttpInvocation(httpHandler, httpHandlerInterceptor, httpReq())) as IHttpHandler
	}

	private fun httpReq(): HttpReq {
		return HttpReq.Builder().url(reqUrl).reqMethod(reqMethod).reqTag(reqTag).queryMap(queryMap).fieldMap(fieldMap)
			.headerMap(headerMap).multipartBody(multipartBody).isMultiPart(isMultiPart).jsonString(jsonString)
			.build()
	}

	private fun <T> request(transform: (data: String) -> T): HttpResult<T> {
		return run {
			val httpResp = httpHandler.execute(httpReq())
			if (httpResp.isSuccessful) {
				HttpResult.success(transform(httpResp.resp!!), httpResp.code, httpResp.headers!!)
			} else {
				if (httpResp.exception != null) {
					HttpResult.throwable(httpResp.exception)
				} else {
					HttpResult.error(httpResp.resp!!, httpResp.code, httpResp.headers!!)
				}
			}
		}
	}

	fun <T> execute(transform: (data: String) -> T): HttpResult<T> {
		return request { data -> transform(data) }
	}

	fun cancelRequest() {
		httpHandler.cancel()
	}

	internal class HttpInvocation constructor(var any: Any, var httpInterceptor: HttpInterceptor, var httpReq: HttpReq) : InvocationHandler {
		override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
			return if (method?.name == "execute") {
				val req = httpInterceptor.beforeExecute(httpReq)
				val obj = method.invoke(any, req)
				httpInterceptor.afterExecute(req, obj as HttpResp)
			} else {
				method!!.invoke(any, args)
			}
		}
	}
}