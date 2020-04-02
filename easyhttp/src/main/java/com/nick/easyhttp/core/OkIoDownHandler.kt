package com.nick.easyhttp.core

import okhttp3.internal.closeQuietly
import okio.*
import java.io.File
import java.io.InputStream

class OkIoDownHandler : IDownloadHandler {

	@Volatile private var isCanceled = false

	override fun saveFile(inputStream: InputStream, file: File, breakPoint: Boolean, contentLength: Long, listener: (state: IDownloadHandler.DownloadState) -> Unit) {
		if (!file.exists() || !breakPoint) {
			file.delete()
			file.createNewFile()
		}
		val downloadState: IDownloadHandler.DownloadState = IDownloadHandler.DownloadState(file.length(),
			file.length() + contentLength, finished = false, canceled = false)
		file.appendingSink().buffer().writeAll(object : ForwardingSource(inputStream.source()) {
			override fun read(sink: Buffer, byteCount: Long): Long {
				if (downloadState.finished || isCanceled) {
					sink.closeQuietly()
					inputStream.closeQuietly()
					return 0
				}
				val readCount = super.read(sink, byteCount)
				listener(downloadState.apply {
					current += readCount
					finished = total == current
					canceled = isCanceled
				})
				return readCount
			}
		})
	}

	override fun cancel() {
		isCanceled = true
	}
}