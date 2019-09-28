package com.nick.lib.network.interfaces

interface HttpConfig {
	fun baseUrl(): String
	fun headers(): HashMap<String, String>
}