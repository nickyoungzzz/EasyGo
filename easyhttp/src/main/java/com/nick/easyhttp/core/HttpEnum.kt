package com.nick.easyhttp.core

enum class ReqMethod constructor(var method: String, var form: Boolean) {
	GET("GET", false), POST("POST", false), PUT("PUT", false), DELETE("DELETE", false),
	PATCH("PATCH", false), HEAD("HEAD", false), GET_FORM("GET", true), POST_FORM("POST", true),
	PUT_FORM("PUT", true), DELETE_FORM("DELETE", true), PATCH_FORM("PATCH", true)
}

enum class HttpStatus {
	SUCCESS, ERROR, EXCEPTION
}

enum class HttpCacheStrategy {
	MEMORY_CACHE, FILE_CACHE
}