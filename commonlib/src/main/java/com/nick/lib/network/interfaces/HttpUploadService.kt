package com.nick.lib.network.interfaces

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.*
import java.util.*

interface HttpUploadService {

	@Multipart
	@POST
	fun upload(@Url url: String, @PartMap partMap: HashMap<String, RequestBody>): Observable<Result<Response>>

	@FormUrlEncoded
	@POST
	fun upload(@Url url: String, base64Url: String): Observable<Result<Response>>

	@PUT
	fun upload(@Url url: String, @Body requestBody: RequestBody): Observable<Result<Response>>
}