package com.nick.lib.network.interfaces

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.*
import java.util.*

internal interface HttpProcessorService {

	@GET
	fun get(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Tag tag: Any?): Observable<Result<String>>

	@POST
	fun post(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Observable<Result<String>>

	@GET
	@FormUrlEncoded
	fun getForm(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @FieldMap(encoded = true) fieldMap: HashMap<String, String>, @Tag tag: Any?): Observable<Result<String>>

	@PUT
	fun put(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Observable<Result<String>>

	@DELETE
	fun delete(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Observable<Result<String>>
}