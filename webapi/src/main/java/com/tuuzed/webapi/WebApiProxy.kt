package com.tuuzed.webapi

import okhttp3.OkHttpClient
import java.lang.reflect.Proxy
import java.nio.charset.Charset
import java.text.SimpleDateFormat

class WebApiProxy @JvmOverloads constructor(
    private val baseUrl: () -> String,
    private val client: OkHttpClient = OkHttpClient.Builder().build(),
    private val callAdapter: CallAdapter = DefaultCallAdapter(),
    private val logger: Logger? = null,
    private val charset: Charset = Charsets.UTF_8,
    private val dateToString: DateToString = { SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it) },
    private val requestConverter: RequestConverter = RequestConverterImpl(baseUrl, logger, charset, dateToString),
    private val responseConverter: ResponseConverter = DefaultResponseConverter()
) {

    fun <T> create(webApiClazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            webApiClazz.classLoader,
            arrayOf(webApiClazz)
        ) { _, method, args ->
            val request = requestConverter(webApiClazz, method, args)
            return@newProxyInstance callAdapter(responseConverter, client.newCall(request))
        } as T
    }

}