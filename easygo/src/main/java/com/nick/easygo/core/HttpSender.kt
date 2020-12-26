package com.nick.easygo.core

import com.nick.easygo.core.download.DownState
import com.nick.easygo.result.HttpResult

interface HttpSender<T> {
	fun send(): HttpResult<T>
	fun download(exc: ((e: Throwable) -> Unit)?, download: ((downState: DownState) -> Unit)?)
}