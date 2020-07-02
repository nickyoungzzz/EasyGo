package com.nick.easyhttp.core.download

import java.io.File

class DownloadState constructor(var current: Long, var total: Long, var finished: Boolean, var canceled: Boolean)
class DownloadParam {

	internal var desSource: File = File("")

	internal var breakPoint: Boolean = false

	fun source(desSource: String) {
		this.desSource = File(desSource)
	}

	fun breakpoint(breakPoint: Boolean) {
		this.breakPoint = breakPoint
	}
}
