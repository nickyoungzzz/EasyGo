package com.nick.lib.network.interfaces

import okhttp3.OkHttpClient

interface HttpConfig {
	fun baseUrl(): String = "https://www.baidu.com/"
	fun okHttpConfig(okHttpBuilder: OkHttpClient.Builder): OkHttpClient {
		return okHttpBuilder.build()
	}
}