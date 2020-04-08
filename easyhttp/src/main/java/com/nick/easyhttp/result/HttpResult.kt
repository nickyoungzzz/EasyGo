package com.nick.easyhttp.result

import com.nick.easyhttp.core.ResponseStatus

class HttpResult<T> constructor(private val status: ResponseStatus) {

	var code: Int = 0
		private set
	var headers: List<Pair<String, String>>? = null
		private set
	var success: T? = null
		private set
	var error: String? = null
		private set
	var throwable: Throwable? = null
		private set

	constructor(status: ResponseStatus, t: T, code: Int, headers: List<Pair<String, String>>) : this(status) {
		this.success = t
		this.code = code
		this.headers = headers
	}

	constructor(status: ResponseStatus, code: Int, f: String, headers: List<Pair<String, String>>) : this(status) {
		this.error = f
		this.code = code
		this.headers = headers
	}

	constructor(status: ResponseStatus, throwable: Throwable?) : this(status) {
		this.throwable = throwable
	}

	fun isSuccess() = status == ResponseStatus.SUCCESS

	fun isError() = status == ResponseStatus.ERROR

	fun isThrowable() = status == ResponseStatus.THROWABLE

	companion object {

		fun <T> success(t: T, code: Int, headers: List<Pair<String, String>>): HttpResult<T> {
			return HttpResult(ResponseStatus.SUCCESS, t, code, headers)
		}

		fun <T> error(f: String, code: Int, headers: List<Pair<String, String>>): HttpResult<T> {
			return HttpResult(ResponseStatus.ERROR, code, f, headers)
		}

		fun <T> throwable(throwable: Throwable?): HttpResult<T> {
			return HttpResult(ResponseStatus.THROWABLE, throwable)
		}
	}

}
