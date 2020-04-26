package com.nick.easyhttp.core.download

import java.io.InputStream

interface DownloadHandler {

	fun saveFile(inputStream: InputStream, downloadParam: DownloadParam,
	             contentLength: Long, listener: (state: DownloadState) -> Unit
	)

	fun cancel()

	companion object {
		@JvmField
		val OKIO_DOWNLOAD_HANDLER = OkIoDownHandler()
	}
}