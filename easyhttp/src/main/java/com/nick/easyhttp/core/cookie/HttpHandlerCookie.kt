package com.nick.easyhttp.core.cookie

class HttpHandlerCookie internal constructor(builder: Builder) {

	var name: String = builder.name
	var value: String = builder.value
	var domain: String = builder.domain
	var path: String = builder.path
	var secure: Boolean = builder.secure
	var httpOnly: Boolean = builder.httpOnly
	var maxAge: Long = builder.maxAge
	var whenCreated: Long = builder.whenCreated

	fun newBuilder() = Builder(this)

	class Builder constructor() {

		internal var name: String = ""
		internal var value: String = ""
		internal var domain: String = ""
		internal var path: String = "/"
		internal var secure: Boolean = false
		internal var httpOnly: Boolean = false
		internal var maxAge: Long = 0
		internal var whenCreated: Long = -1L

		constructor(httpHandlerCookie: HttpHandlerCookie) : this() {
			this.name = httpHandlerCookie.name
			this.value = httpHandlerCookie.value
			this.domain = httpHandlerCookie.domain
			this.path = httpHandlerCookie.path
			this.secure = httpHandlerCookie.secure
			this.httpOnly = httpHandlerCookie.httpOnly
			this.maxAge = httpHandlerCookie.maxAge
			this.whenCreated = httpHandlerCookie.whenCreated
		}

		fun name(name: String) = apply { this.name = name }

		fun value(value: String) = apply { this.value = value }

		fun domain(domain: String) = apply { this.domain = domain }

		fun path(path: String) = apply { this.path = path }

		fun secure(secure: Boolean) = apply { this.secure = secure }

		fun httpOnly(httpOnly: Boolean) = apply { this.httpOnly = httpOnly }

		fun maxAge(maxAge: Long) = apply { this.maxAge = maxAge }

		fun whenCreated(whenCreated: Long) = apply { this.whenCreated = whenCreated }

		fun build() = HttpHandlerCookie(this)
	}
}