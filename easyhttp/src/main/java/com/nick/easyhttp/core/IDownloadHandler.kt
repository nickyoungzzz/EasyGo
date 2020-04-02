package com.nick.easyhttp.core

import android.os.Environment
import java.io.File
import java.io.InputStream

interface IDownloadHandler {
	fun saveFile(inputStream: InputStream, file: File, breakPoint: Boolean,
	             contentLength: Long, listener: (state: DownloadState) -> Unit
	)
	fun cancel()
	class DownloadState constructor(var current: Long, var total: Long, var finished: Boolean, var canceled: Boolean)
	class DownloadParam @JvmOverloads constructor(var source: File = File(Environment.getExternalStorageDirectory(), "default.d"),
	                                              var breakPoint: Boolean = false
	)
}