package com.nick.easyhttp.config

class TimeoutConfig constructor(builder: Builder) {

    var urlSegment = builder.urlSegment
    var tag: Any? = builder.tag
    var connectTimeout = builder.connectTimeOut
    var readTimeOut = builder.readTimeOut
    var writeTimeOut = builder.writeTimeOut

    fun newBuilder() = Builder(this)

    constructor() : this(Builder())

    companion object {
        const val TIMEOUT = 15000L
        val DEFAULT_TIMEOUT: TimeoutConfig = TimeoutConfig()
    }

    class Builder constructor() {

        internal var urlSegment = ""
        internal var tag: Any? = null
        internal var connectTimeOut: Long = TIMEOUT
        internal var readTimeOut: Long = TIMEOUT
        internal var writeTimeOut: Long = TIMEOUT

        constructor(timeoutConfig: TimeoutConfig) : this() {
            this.urlSegment = timeoutConfig.urlSegment
            this.tag = timeoutConfig.tag
            this.connectTimeOut = timeoutConfig.connectTimeout
            this.readTimeOut = timeoutConfig.readTimeOut
            this.writeTimeOut = timeoutConfig.writeTimeOut
        }

        fun urlSegment(urlSegment: String) = apply { this.urlSegment = urlSegment }

        fun tag(tag: Any) = apply { this.tag = tag }

        fun connectTimeOut(connectTimeOut: Long) = apply { this.connectTimeOut = connectTimeOut }

        fun readTimeOut(readTimeOut: Long) = apply { this.readTimeOut = readTimeOut }

        fun writeTimeOut(writeTimeOut: Long) = apply { this.writeTimeOut = writeTimeOut }

        fun build() = TimeoutConfig(this)
    }
}