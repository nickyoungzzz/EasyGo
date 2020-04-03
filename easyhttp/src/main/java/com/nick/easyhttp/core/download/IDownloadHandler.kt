package com.nick.easyhttp.core.download

import java.io.InputStream

interface IDownloadHandler {

	fun saveFile(inputStream: InputStream, downloadParam: DownloadParam,
	             contentLength: Long, listener: (state: DownloadState) -> Unit
	)

	fun cancel()
}