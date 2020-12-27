package com.nick.easygo.core.res

import com.nick.easygo.core.download.DownState

interface HttpStreamer : HttpExecutor {
	fun download(exc: ((e: Throwable) -> Unit)?, download: ((downState: DownState) -> Unit)?)
}