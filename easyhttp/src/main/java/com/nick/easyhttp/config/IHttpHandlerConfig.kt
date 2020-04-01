package com.nick.easyhttp.config

interface IHttpHandlerConfig {
	fun config()
	fun needConfig(): Boolean
}