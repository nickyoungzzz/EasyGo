package com.nick.easygo.core.res

import com.nick.easygo.result.HttpResult

interface HttpSender<T> : HttpExecutor {
    fun send(): HttpResult<T>
}