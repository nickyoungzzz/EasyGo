package com.nick.easyhttp.core.download

import okio.*
import java.io.InputStream

class OkIoDownHandler : IDownloadHandler {

	@Volatile private var isCanceled = false

	override fun saveFile(inputStream: InputStream, downloadParam: DownloadParam, contentLength: Long,
	                      listener: (state: DownloadState) -> Unit
	) {
		val file = downloadParam.source
		val breakPoint = downloadParam.breakPoint
		if (!file.exists() || !breakPoint) {
			file.delete()
			file.createNewFile()
		}
		val downloadState = DownloadState(file.length(),
			file.length() + contentLength, finished = false, canceled = false)
		file.appendingSink().buffer().writeAll(object : ForwardingSource(inputStream.source()) {

			private var currentP = file.length()

			override fun read(sink: Buffer, byteCount: Long): Long {
				listener(downloadState.apply {
					current = currentP
					finished = total == current
					canceled = isCanceled
				})
				return if (downloadState.finished || isCanceled) {
					sink.close()
					inputStream.close()
					0
				} else {
					val readCount = super.read(sink, byteCount)
					currentP += readCount
					readCount
				}
			}
		})
	}

	override fun cancel() {
		isCanceled = true
	}
}