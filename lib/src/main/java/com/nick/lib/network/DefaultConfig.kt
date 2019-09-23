package com.nick.lib.network

import com.nick.lib.network.interfaces.HttpConfig

class DefaultConfig private constructor() : HttpConfig {

	override fun baseUrl(): String {
		return "https://www.baidu.com"
	}

	companion object {
		fun create() = DefaultConfig()
	}
}