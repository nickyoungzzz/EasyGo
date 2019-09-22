package com.nick.lib.network.interfaces

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.*
import java.util.*

interface HttpProcessorService {

	@GET
	fun get(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>): Observable<Result<String>>

	@POST
	fun post(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody): Observable<Result<String>>

	@GET
	fun getForm(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @FieldMap fieldMap: HashMap<String, String>): Observable<Result<String>>

	@POST
	@FormUrlEncoded
	fun postForm(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @FieldMap fieldMap: HashMap<String, String>): Observable<Result<String>>

	@PUT
	fun put(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>): Observable<Result<String>>
}