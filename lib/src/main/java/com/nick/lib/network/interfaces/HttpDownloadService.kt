package com.nick.lib.network.interfaces

import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface HttpDownloadService {

	@GET
	@Streaming
	fun download(@Url url: String)
}