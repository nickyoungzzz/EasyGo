package com.nick.easygo.core.download

import java.io.File

class DownState constructor(
    var current: Long,
    var total: Long,
    var finished: Boolean,
    var canceled: Boolean
)

class DownParam {

    internal var desSource: File = File("")
    internal var breakPoint: Boolean = false

    fun source(desSource: String) {
        this.desSource = File(desSource)
    }

    fun breakpoint(breakPoint: Boolean) {
        this.breakPoint = breakPoint
    }
}
