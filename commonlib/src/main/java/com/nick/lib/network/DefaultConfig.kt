package com.nick.lib.network

import com.nick.lib.network.interfaces.HttpConfig

class DefaultConfig private constructor() : HttpConfig {
	override fun headers(): HashMap<String, String> {
		val commonHeaderMap = hashMapOf<String, String>()
		commonHeaderMap["Content-Type"] = "application/json"
		return commonHeaderMap
	}

	override fun baseUrl(): String {
		return "https://www.baidu.com/"
	}

	companion object {
		fun create() = DefaultConfig()
	}
}