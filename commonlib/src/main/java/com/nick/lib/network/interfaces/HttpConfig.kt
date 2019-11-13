package com.nick.lib.network.interfaces

import okhttp3.OkHttpClient

class HttpConfig {
	fun baseUrl(): String = "https://www.baidu.com/"
	fun okHttpConfig(okHttpBuilder: OkHttpClient.Builder): OkHttpClient = okHttpBuilder.build()
}