package com.nick.easyhttp.result

import java.io.InputStream

class HttpResp internal constructor(var resp: String, var code: Int, var isSuccessful: Boolean,
                                    val headers: Map<String, List<String>>, var exception: Throwable?,
                                    val contentLength: Long, val inputStream: InputStream?
)