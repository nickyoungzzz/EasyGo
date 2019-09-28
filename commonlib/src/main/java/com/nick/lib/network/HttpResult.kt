package com.nick.lib.network

import com.nick.lib.network.interfaces.ResponseStatus
import okhttp3.Headers

class HttpResult<T, F> constructor(var status: ResponseStatus) {

	var code: Int = 0
	var headers: Headers? = null
	var success: T? = null
	var error: F? = null
	var throwable: Throwable? = null

	constructor(status: ResponseStatus, t: T, code: Int, headers: Headers) : this(status) {
		this.success = t
		this.code = code
		this.headers = headers
	}

	constructor(status: ResponseStatus, code: Int, f: F, headers: Headers) : this(status) {
		this.error = f
		this.code = code
		this.headers = headers
	}

	constructor(status: ResponseStatus, throwable: Throwable?) : this(status) {
		this.throwable = throwable
	}

	fun isThrowable() = throwable != null

	companion object {

		fun <T, F> success(t: T, code: Int, headers: Headers): HttpResult<T, F> {
			return HttpResult(ResponseStatus.SUCCESS, t, code, headers)
		}

		fun <T, F> error(f: F, code: Int, headers: Headers): HttpResult<T, F> {
			return HttpResult(ResponseStatus.ERROR, code, f, headers)
		}

		fun <T, F> throwable(throwable: Throwable?): HttpResult<T, F> {
			return HttpResult(ResponseStatus.ERROR, throwable)
		}

		fun <T, F> loading(): HttpResult<T, F> {
			return HttpResult(ResponseStatus.LOADING)
		}
	}
}
