package com.nick.easyhttp.core.cache

import java.io.Serializable
import java.net.CacheResponse

class HttpCache private constructor(builder: Builder) : Serializable {

    var cacheResponse: CacheResponse? = builder.cacheResponse
    var maxAge: Long = builder.maxAge
    var noCache = builder.noCache
    var noStore = builder.noStore
    var isPrivate = builder.isPrivate
    var isPublic = builder.isPublic
    var onlyIfCached = builder.onlyIfCached
    var maxStale = builder.maxStale
    var mustRevalidate = builder.mustRevalidate
    var cacheCreated = builder.cacheCreated

    class Builder constructor() {
        internal var cacheResponse: CacheResponse? = null
        internal var maxAge = -1L
        internal var noCache = false
        internal var noStore = false
        internal var isPrivate = false
        internal var isPublic = false
        internal var onlyIfCached = false
        internal var maxStale = -1L
        internal var mustRevalidate = false
        internal var cacheCreated = -1L

        fun cacheResponse(cacheResponse: CacheResponse) = apply { this.cacheResponse = cacheResponse }

        fun maxAge(maxAge: Long) = apply { this.maxAge = maxAge }

        fun noCache() = apply { this.noCache = true }

        fun noStore() = apply { this.noStore = true }

        fun isPrivate() = apply { this.isPrivate = true }

        fun isPublic() = apply { isPublic = true }

        fun onlyIfCached() = apply { onlyIfCached = true }

        fun maxStale(maxStale: Long) = apply { this.maxStale = maxStale }

        fun mustRevalidate() = apply { this.mustRevalidate = true }

        fun cacheCreated(cacheCreated: Long) = apply { this.cacheCreated = cacheCreated }

        fun build() = HttpCache(this)
    }
}