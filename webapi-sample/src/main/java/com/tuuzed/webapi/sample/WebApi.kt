package com.tuuzed.webapi.sample

import com.tuuzed.webapi.Call
import com.tuuzed.webapi.http.*
import okhttp3.Response

interface WebApi {

    @Endpoint("/get", method = "GET")
    fun get(
        @Query("query") query: String,
        @QueryMap queries: Map<String, String>,
        @Header("x-header") header: String,
        @HeaderMap headers: Map<String, String>
    ): Call<Response>


    @Endpoint("/post", method = "POST")
    fun post(
        @Query("query") query: String,
        @QueryMap queries: Map<String, String>,
        @Header("x-header") header: String,
        @HeaderMap headers: Map<String, String>
    ): Call<Response>


}