package com.nick.easyhttp.inject

import com.nick.easyhttp.core.req.okhttp.OkHttpHandler
import com.nick.easyhttp.core.req.urlconnection.UrlConnectionHandler
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [HttpHandlerModule::class])
interface HttpHandlerComponent {

    fun inject(okHttpHandler: OkHttpHandler)

    fun inject(urlConnectionHandler: UrlConnectionHandler)
}