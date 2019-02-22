package com.tuuzed.common.webapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuuzed.common.webapi.http.Endpoint
import com.tuuzed.common.webapi.internal.IgnoreAnnotationExclusionStrategy
import okhttp3.OkHttpClient
import java.lang.reflect.Proxy

class WebApiProxy @JvmOverloads constructor(
    private val baseUrl: () -> String,
    gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(),
    private val client: OkHttpClient = OkHttpClient.Builder().build(),
    private val requestBuilder: RequestBuilder = DefaultRequestBuilder("utf-8"),
    callHandlers: Array<CallHandler> = emptyArray()
) {

    private val defaultCallHandler = DefaultCallHandler()
    private val callHandlerList = listOf(*callHandlers)
    private val webApiGson: Gson = gson.newBuilder()
        .setExclusionStrategies(IgnoreAnnotationExclusionStrategy())
        .create()

    fun <T> create(webApiClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            webApiClass.classLoader,
            arrayOf(webApiClass)
        ) { _, method, args ->
            requestBuilder
                .buildRequest(baseUrl(), webApiGson, webApiClass, method, args)
                .let { request ->
                    var callHandler: CallHandler = defaultCallHandler
                    callHandlerList.forEach { handler ->
                        if (handler.isSupported(webApiClass, method, args)) {
                            callHandler = handler
                            return@forEach
                        }
                    }
                    return@let callHandler.handleCall(
                        client.newCall(request),
                        webApiGson,
                        webApiClass,
                        method,
                        args
                    )
                }
        } as T
    }
}