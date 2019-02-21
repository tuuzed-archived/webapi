package com.tuuzed.common.webapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuuzed.common.webapi.internal.IgnoreAnnotationExclusionStrategy
import okhttp3.OkHttpClient
import java.lang.reflect.Proxy

class WebApiProxy @JvmOverloads constructor(
    private val baseUrl: () -> String,
    gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(),
    private val client: OkHttpClient = OkHttpClient.Builder().build(),
    private val requestBuilder: RequestBuilder = DefaultRequestBuilder("utf-8"),
    private val responseHandler: CallHandler = DefaultCallHandler()
) {

    private val webApiGson: Gson = gson.newBuilder()
        .setExclusionStrategies(IgnoreAnnotationExclusionStrategy())
        .create()

    fun <T> create(webApiClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            webApiClass.classLoader,
            arrayOf(webApiClass)
        ) { _, method, args ->
            return@newProxyInstance requestBuilder
                .buildRequest(baseUrl, webApiGson, webApiClass, method, args)
                .let { responseHandler.handleCall(client.newCall(it), webApiGson, webApiClass, method, args) }
        } as T
    }
}