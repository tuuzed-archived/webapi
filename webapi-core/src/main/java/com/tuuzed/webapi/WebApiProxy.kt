package com.tuuzed.webapi

import okhttp3.OkHttpClient
import java.lang.reflect.Proxy
import java.text.SimpleDateFormat

class WebApiProxy @JvmOverloads constructor(
    private val baseUrl: () -> String,
    private val client: OkHttpClient = OkHttpClient.Builder().build(),
    private val callAdapter: CallAdapter = DefaultCallAdapter(),
    private val converter: Converter = DefaultConverter(),
    private val requestBuilder: RequestBuilder = DefaultRequestBuilder(
        baseUrl = baseUrl,
        charset = Charsets.UTF_8,
        dateToString = { SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it) }
    )
) {

    companion object {
        var logger: Logger? = null
        @JvmStatic
        fun registerLogger(logger: Logger?) {
            this.logger = logger
        }
    }

    fun <T> create(webApiClazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            webApiClazz.classLoader,
            arrayOf(webApiClazz)
        ) { _, method, args ->
            val request = requestBuilder.invoke(webApiClazz, method, args)
            return@newProxyInstance callAdapter.invoke(method, converter, client.newCall(request))
        } as T
    }

}