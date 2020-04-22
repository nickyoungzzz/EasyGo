package com.nick.easyhttp.core.cookie

import java.net.URI

interface IHttpCookieHandler {

	fun shouldSaveCookie(uri: URI, cookie: HttpHandlerCookie): Boolean
	fun useCookieForRequest(uri: URI): Boolean
	fun maxCookieCount(uri: URI): Int

	companion object {

		private const val MAX_COOKIE_COUNT = 50

		val NO_COOKIE = object : IHttpCookieHandler {
			override fun shouldSaveCookie(uri: URI, cookie: HttpHandlerCookie): Boolean {
				return false
			}

			override fun useCookieForRequest(uri: URI): Boolean {
				return false
			}

			override fun maxCookieCount(uri: URI): Int {
				return MAX_COOKIE_COUNT
			}
		}
	}
}