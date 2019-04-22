package com.tuuzed.webapi

import com.tuuzed.webapi.converter.GsonConverter
import com.tuuzed.webapi.sample.WebApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test

class Sample {

    lateinit var api: WebApi

    @Before
    fun create() {
        WebApiProxy.registerLogger {
            System.out.println("WebApiProxy=> $it")
        }
        api = WebApiProxy(
            client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message ->
                        System.err.println("HttpClient=> $message")
                    }).also { it.level = HttpLoggingInterceptor.Level.BODY }
                ).build(),
            baseUrl = { "http://localhost:8080" },
            converter = GsonConverter()
        ).create(WebApi::class.java)
    }

    @Test
    fun get() {
        val res = api.get(
            query = "qa",
            queries = mapOf(Pair("qkey", "qval")),
            header = "ha",
            headers = mapOf(Pair("x-hkey", "hval"))
        ).execute()
        println("========================================")
        println(res)
        println("========================================")
    }
}