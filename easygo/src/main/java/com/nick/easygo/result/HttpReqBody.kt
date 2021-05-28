package com.nick.easygo.result

class HttpReqBody internal constructor(
    val fieldMap: Map<String, String>, val multipartBody: Map<String, Any>,
    val isMultiPart: Boolean, val jsonString: String,
) {
    fun newBuilder() = Builder(this)

    class Builder internal constructor(httpReqBody: HttpReqBody) {
        private val realFieldMap = HashMap<String, String>()
        private val realMultipartBody = HashMap<String, Any>()
        private var isMultiPart = httpReqBody.isMultiPart
        private var jsonString = httpReqBody.jsonString

        init {
            httpReqBody.fieldMap.forEach { (key, value) ->
                realFieldMap[key] = value
            }
            httpReqBody.multipartBody.forEach { (key, value) ->
                realMultipartBody[key] = value
            }
        }

        fun isMultiPart(isMultiPart: Boolean) = apply { this.isMultiPart = isMultiPart }

        fun jsonString(jsonString: String) = apply { this.jsonString = jsonString }

        fun addField(key: String, value: String) = apply {
            if (!realFieldMap.containsKey(key)) {
                realFieldMap[key] = value
            }
        }

        fun field(key: String, value: String) = apply {
            if (realFieldMap.containsKey(key)) {
                realFieldMap[key] = value
            }
        }

        fun removeField(key: String) = apply {
            realFieldMap.remove(key)
        }

        fun addPart(key: String, value: String) = apply {
            if (!realMultipartBody.containsKey(key)) {
                realMultipartBody[key] = value
            }
        }

        fun part(key: String, value: String) = apply {
            if (realMultipartBody.containsKey(key)) {
                realMultipartBody[key] = value
            }
        }

        fun removePart(key: String) = apply {
            realMultipartBody.remove(key)
        }

        fun build() = HttpReqBody(realFieldMap, realMultipartBody, isMultiPart, jsonString)
    }
}