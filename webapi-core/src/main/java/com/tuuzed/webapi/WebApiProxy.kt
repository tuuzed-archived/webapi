package com.tuuzed.webapi

import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.text.SimpleDateFormat

class WebApiProxy @JvmOverloads constructor(
    private val baseUrl: () -> String,
    private val client: OkHttpClient = OkHttpClient.Builder().build(),
    private val callAdapters: List<CallAdapter<*>> = listOf(DefaultCallAdapter()),
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
            return@newProxyInstance adapterInvoke(method, request)
        } as T
    }

    private fun adapterInvoke(method: Method, request: Request): Any? {
        return findCallAdapter(method)?.invoke(method, converter, client.newCall(request))
            ?: throw RuntimeException("call adapter error")
    }

    private val cacheMethodCallAdapter = hashMapOf<Method, CallAdapter<*>>()

    private fun findCallAdapter(method: Method): CallAdapter<*>? {
        val callAdapter = cacheMethodCallAdapter[method]
        if (callAdapter != null) {
            return callAdapter
        } else {
            val returnType = method.genericReturnType as ParameterizedType
            callAdapters.forEach { adapter ->
                val supportedType = adapter::class.java.genericInterfaces[0].let {
                    if (it is ParameterizedType) {
                        val type = it.actualTypeArguments[0]
                        if (type is ParameterizedType) {
                            type
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                if (supportedType?.rawType == returnType.rawType) {
                    cacheMethodCallAdapter[method] = adapter
                    return adapter
                }
            }
            return null
        }
    }

}