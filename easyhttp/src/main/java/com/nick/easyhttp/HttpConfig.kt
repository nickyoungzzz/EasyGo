package com.nick.easyhttp

import okhttp3.OkHttpClient

open class HttpConfig {
	open fun baseUrl(): String = "https://www.baidu.com/"
	open fun okHttpConfig(okHttpBuilder: OkHttpClient.Builder): OkHttpClient = okHttpBuilder.build()
}