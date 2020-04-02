package com.nick.easyhttp.core

import okio.*
import java.io.File
import java.io.InputStream

class OkIoDownHandler : IDownloadHandler {

	@Volatile private var isCanceled = false

	override fun saveFile(inputStream: InputStream, file: File, breakPoint: Boolean, contentLength: Long,
	                      listener: (state: IDownloadHandler.DownloadState) -> Unit
	) {
		if (!file.exists() || !breakPoint) {
			file.delete()
			file.createNewFile()
		}
		val downloadState: IDownloadHandler.DownloadState = IDownloadHandler.DownloadState(file.length(),
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
		println("cancel")
		isCanceled = true
		println(isCanceled)
	}
}