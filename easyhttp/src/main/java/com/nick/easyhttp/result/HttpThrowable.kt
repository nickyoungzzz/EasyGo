package com.nick.easyhttp.result

import okhttp3.Headers

class HttpThrowable @JvmOverloads constructor(errorMessage: String, var code: Int = 0, var headers: Headers? = null) : Throwable(errorMessage)