package com.nick.easyhttp.core.download

import okio.*
import java.io.InputStream

class OkIoDownHandler : DownloadHandler {

	@Volatile private var isCanceled = false

	override fun saveFile(inputStream: InputStream, downParam: DownParam, contentLength: Long,
	                      listener: (state: DownState) -> Unit
	) {
		val file = downParam.desSource
		val breakPoint = downParam.breakPoint
		if (!file.exists() || !breakPoint) {
			file.delete()
			file.createNewFile()
		}
		val downloadState = DownState(file.length(),
			file.length() + contentLength, finished = false, canceled = false)
		file.appendingSink().buffer().writeAll(object : ForwardingSource(inputStream.source()) {

			private var currentP = file.length()

			override fun read(sink: Buffer, byteCount: Long): Long {
				listener(downloadState.apply {
					current = currentP
					finished = total == current
					canceled = isCanceled
				})
				return if (isCanceled || byteCount == 0L) {
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