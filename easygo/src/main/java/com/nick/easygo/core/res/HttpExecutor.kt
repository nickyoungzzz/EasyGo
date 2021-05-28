package com.nick.easygo.core.res

import com.nick.easygo.result.HttpResp

interface HttpExecutor {
    fun execute(): HttpResp
}