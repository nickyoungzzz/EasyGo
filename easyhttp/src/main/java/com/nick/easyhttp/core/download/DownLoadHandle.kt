package com.nick.easyhttp.core.download

import android.os.Environment
import java.io.File

class DownloadState constructor(var current: Long, var total: Long, var finished: Boolean, var canceled: Boolean)
class DownloadParam @JvmOverloads constructor(var source: File = File(Environment.getExternalStorageDirectory(), "default.temp"),
                                              var breakPoint: Boolean = false
)