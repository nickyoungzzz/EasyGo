package com.nick.easyhttp.internal

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

internal interface HttpService {

	@GET
	fun get(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Tag tag: Any?): Call<String>

	@POST
	fun post(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Call<String>

	@GET
	@FormUrlEncoded
	fun getForm(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @FieldMap(encoded = true) fieldMap: HashMap<String, String>, @Tag tag: Any?): Call<String>

	@PUT
	fun put(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Call<String>

	@DELETE
	fun delete(@Url url: String, @HeaderMap headerMap: HashMap<String, String>, @QueryMap queryMap: HashMap<String, String>, @Body requestBody: RequestBody, @Tag tag: Any?): Call<String>
}