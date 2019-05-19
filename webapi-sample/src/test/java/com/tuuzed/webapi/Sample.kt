package com.tuuzed.webapi

import com.tuuzed.webapi.adapter.RxJava2CallAdapter
import com.tuuzed.webapi.converter.GsonConverter
import com.tuuzed.webapi.coroutines.await
import com.tuuzed.webapi.sample.WebApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            converter = GsonConverter(),
            callAdapters = listOf(RxJava2CallAdapter(), DefaultCallAdapter())
        ).create(WebApi::class.java)
    }

    @Test
    fun get() {
        api.get(
            query = "qa",
            queries = mapOf(Pair("qkey", "qval")),
            header = "ha",
            headers = mapOf(Pair("x-hkey", "hval"))
        ).subscribe {
            println("========================================")
            println(it)
            println("========================================")
        }
    }

    @Test
    fun get2() {
        GlobalScope.launch {
            val res = api.get2(
                query = "qa",
                queries = mapOf(Pair("qkey", "qval")),
                header = "ha",
                headers = mapOf(Pair("x-hkey", "hval"))
            ).await()
            println("========================================")
            println(res)
            println("========================================")
        }
        System.`in`.read()
    }
}