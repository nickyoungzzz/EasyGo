package com.nick.easyhttp.core.cache

import com.nick.easyhttp.core.HttpCacheStrategy
import java.io.File

class HttpCacheHandler constructor(var cacheStrategy: HttpCacheStrategy, var cacheCount: Int, var fileCache: File) {
	companion object {
		val MEMORY_CACHE = HttpCacheHandler(HttpCacheStrategy.MEMORY_CACHE, 50, File(""))
	}
}
