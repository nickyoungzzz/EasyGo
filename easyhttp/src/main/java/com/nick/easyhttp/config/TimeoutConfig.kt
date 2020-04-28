package com.nick.easyhttp.config

class TimeoutConfig constructor(builder: Builder) {

	var connectTimeout = builder.connectTimeOut
	var readTimeOut = builder.readTimeOut
	var writeTimeOut = builder.writeTimeOut

	fun newBuilder() = Builder(this)

	constructor() : this(Builder())

	companion object {
		const val TIMEOUT = 15000L
		val DEFAULT_CONFIG: TimeoutConfig = TimeoutConfig()
	}

	class Builder constructor() {

		internal var connectTimeOut: Long = TIMEOUT
		internal var readTimeOut: Long = TIMEOUT
		internal var writeTimeOut: Long = TIMEOUT

		constructor(timeoutConfig: TimeoutConfig) : this() {
			this.connectTimeOut = timeoutConfig.connectTimeout
			this.readTimeOut = timeoutConfig.readTimeOut
			this.writeTimeOut = timeoutConfig.writeTimeOut
		}

		fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

		fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

		fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

		fun build() = TimeoutConfig(this)
	}
}