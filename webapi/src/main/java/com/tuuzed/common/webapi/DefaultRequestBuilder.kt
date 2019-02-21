package com.tuuzed.common.webapi

import com.google.gson.Gson
import com.tuuzed.common.webapi.http.*
import com.tuuzed.common.webapi.internal.EMPTY_REQUEST
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import java.lang.reflect.Method
import java.net.URLEncoder
import java.util.*

class DefaultRequestBuilder(
    private val charset: String
) : RequestBuilder {

    override fun buildRequest(
        baseUrl: () -> String,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>
    ): Request {
        val endpoint = method.getAnnotation(Endpoint::class.java)
            ?: throw NullPointerException("调用方法没有使用@Endpoint标注")

        val endpointMethod = endpoint.method
        var endpointValue = endpoint.value
        val encodedQueryMap = HashMap<String, String>()
        val headersBuilder = Headers.Builder()

        val fieldMap = HashMap<String, String>()
        val isFormUrlEncoded = method.getAnnotation(FormUrlEncoded::class.java) != null
        var requestBody: RequestBody? = null

        // Headers
        webApiClass.getAnnotation(Header::class.java)?.let {
            if (it.line.contains(":")) {
                headersBuilder.add(it.line)
            }
        }
        method.getAnnotation(Header::class.java)?.let {
            if (it.line.contains(":")) {
                headersBuilder.add(it.line)
            }
        }

        val parameterAnnotations = method.parameterAnnotations
        parameterAnnotations.forEachIndexed { index, arrayOfAnnotations ->
            val arg = args[index]
            arrayOfAnnotations.forEach {
                when (it) {
                    is Path -> endpointValue = endpointValue.replacePlaceholderAny(it.name, arg, it.encoded)
                    is Query -> encodedQueryMap.putAnyUrlEncode(it.name, arg, true, it.encoded)
                    is QueryMap -> encodedQueryMap.putAllAnyUrlEncode(arg, true, it.encoded)
                    is Header -> headersBuilder.addAny(it.name, arg)
                    is HeaderMap -> headersBuilder.addAllAny(arg)
                    is Field -> fieldMap.putAnyUrlEncode(it.name, arg, isFormUrlEncoded, it.encoded)
                    is FieldMap -> fieldMap.putAllAnyUrlEncode(arg, isFormUrlEncoded, it.encoded)
                    is RawBody -> {
                        if (arg is RequestBody) {
                            requestBody = arg
                        }
                    }
                }
            }
        }
        if (HttpMethod.permitsRequestBody(endpointMethod)) {
            if (requestBody == null) {
                requestBody = if (isFormUrlEncoded) {
                    val formBodyBuilder = FormBody.Builder()
                    fieldMap.forEach { formBodyBuilder.addEncoded(it.key, it.value) }
                    formBodyBuilder.build()
                } else {
                    RequestBody.create(
                        MediaType.parse("application/json; charset=$charset"),
                        gson.toJson(fieldMap)
                    )
                }
            }
        }
        val urlBuilder = StringBuilder(baseUrl())
        if (!urlBuilder.endsWith("/") && !endpointValue.startsWith("/")) {
            urlBuilder.append("/")
        }
        urlBuilder.append(endpointValue)
        if (encodedQueryMap.isNotEmpty()) {
            if (urlBuilder.contains("?")) {
                if (!urlBuilder.endsWith("?") || !urlBuilder.endsWith("&")) {
                    urlBuilder.append("&")
                }
                urlBuilder.append(encodedQueryMap.toQueryString())
            } else {
                urlBuilder.append("?").append(encodedQueryMap.toQueryString())
            }
        }

        val requestBuilder = Request.Builder()
            .method(
                endpointMethod,
                if (HttpMethod.permitsRequestBody(endpointMethod)) requestBody ?: EMPTY_REQUEST
                else null
            )
            .url(urlBuilder.toString())
        headersBuilder.build().let { if (it.size() != 0) requestBuilder.headers(it) }
        return requestBuilder.build()
    }

    // Ext Methods
    private fun String.replacePlaceholderAny(name: String, value: Any?, encoded: Boolean): String {
        return this.replace(
            "{$name}",
            when (value) {
                is String -> if (!encoded) URLEncoder.encode(value, charset) else value
                null -> ""
                else -> if (!encoded) URLEncoder.encode("$value", charset) else "$value"
            }
        )
    }

    private fun HashMap<String, String>.toQueryString(): String {
        val queryString = StringBuilder()
        var first = true
        this.forEach {
            if (!first) queryString.append("&")
            queryString.append(it.key).append("=").append(it.value)
            first = false
        }
        return queryString.toString()
    }


    private fun HashMap<String, String>.putAllAnyUrlEncode(
        value: Any?,
        needEncode: Boolean,
        encoded: Boolean
    ) {
        if (value is Map<*, *>) {
            value.forEach { entry ->
                entry.key?.let {
                    if (it is String) {
                        putAnyUrlEncode(it, entry.value, needEncode, encoded)
                    }
                }
            }
        }
    }

    private fun HashMap<String, String>.putAnyUrlEncode(
        name: String,
        value: Any?,
        needEncode: Boolean,
        encoded: Boolean
    ) {
        if ("" == name) return
        this[name] = when (value) {
            is String -> if (needEncode && !encoded) URLEncoder.encode(value, charset) else value
            null -> ""
            else -> if (needEncode && !encoded) URLEncoder.encode("$value", charset) else "$value"
        }
    }

    private fun Headers.Builder.addAny(name: String, value: Any?) {
        if ("" == name) return
        when (value) {
            is Date -> this.add(name, value)
            is String -> this.add(name, value.trim())
            null -> this.add(name, "")
            else -> this.add(name, "$value".trim())
        }
    }

    private fun Headers.Builder.addAllAny(value: Any?) {
        if (value is Map<*, *>) {
            value.forEach { entry ->
                entry.key?.let {
                    if (it is String) {
                        addAny(it, entry.value)
                    }
                }
            }
        }
    }
}