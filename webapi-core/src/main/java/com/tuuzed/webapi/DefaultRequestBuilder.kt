package com.tuuzed.webapi

import com.tuuzed.webapi.http.*
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Method
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*

internal class DefaultRequestBuilder(
    private val baseUrl: () -> String,
    private val charset: Charset,
    private val dateToString: DateToString
) : RequestBuilder {

    override fun invoke(webApiClazz: Class<*>, method: Method, args: Array<Any>?): Request {
        return createRequest(webApiClazz, method, args)
    }

    private fun createRequest(webApiClazz: Class<*>, method: Method, args: Array<Any>?): Request {
        val endpoint = method.getAnnotation(Endpoint::class.java)
            ?: throw IllegalStateException("Non @Endpoint, method: $method")
        var endpointValue = endpoint.value
        val encodedQueryMap = linkedMapOf<String, String>()
        val headersBuilder = Headers.Builder()
        val fieldMap = linkedMapOf<String, String>()
        val requestBodyList = LinkedList<RequestBody>()
        val fileMap = linkedMapOf<String, File>()
        val fileTypeMap = linkedMapOf<String, String>()
        val isFormUrlEncoded = method.isAnnotationPresent(FormUrlEncoded::class.java)
        webApiClazz.getAnnotation(Header::class.java)?.let { if (it.line.contains(":")) headersBuilder.add(it.line) }
        method.getAnnotation(Header::class.java)?.let { if (it.line.contains(":")) headersBuilder.add(it.line) }
        if (args != null) {
            method.parameterAnnotations.forEachIndexed { index, annotations ->
                val argVal = args[index]
                LOG("argVal: $argVal, annotations: ${Arrays.toString(annotations)}")
                annotations.forEach {
                    when (it) {
                        is Path -> endpointValue = endpointValue.replacePlaceholderAny(it.name, argVal, it.encoded)
                        is Query -> encodedQueryMap.putAnyUrlEncode(it.name, argVal, true, it.encoded, dateToString)
                        is QueryMap -> encodedQueryMap.putAllAnyUrlEncode(argVal, true, it.encoded, dateToString)
                        is Header -> headersBuilder.addAny(it.name, argVal)
                        is HeaderMap -> headersBuilder.addAllAny(argVal)
                        is Field -> fieldMap.putAnyUrlEncode(
                            it.name,
                            argVal,
                            isFormUrlEncoded,
                            it.encoded,
                            dateToString
                        )
                        is FieldMap -> fieldMap.putAllAnyUrlEncode(argVal, isFormUrlEncoded, it.encoded, dateToString)
                        is FileField -> if (argVal is File) {
                            fileMap[it.name] = argVal
                            fileTypeMap[it.name] = it.mediaType
                        }
                        is RawBody -> if (argVal is RequestBody) requestBodyList.add(argVal)
                    }
                }
            }
        }

        val httpMethod: String = endpoint.method
        val requestBody: RequestBody? = if (HttpMethod.permitsRequestBody(httpMethod)) {
            when {
                method.isAnnotationPresent(Multipart::class.java) -> {
                    MultipartBody.Builder().also { builder ->
                        fieldMap.entries.forEach { builder.addFormDataPart(it.key, it.value) }
                        fileMap.entries.forEach {
                            builder.addFormDataPart(
                                it.key,
                                it.value.name,
                                RequestBody.create(
                                    MediaType.parse(fileTypeMap[it.key] ?: "application/octet-stream"),
                                    it.value
                                )
                            )
                        }
                    }.build()
                }
                method.isAnnotationPresent(FormUrlEncoded::class.java) -> {
                    FormBody.Builder().also { builder ->
                        fieldMap.entries.forEach { builder.addEncoded(it.key, it.value) }
                    }.build()
                }
                else -> RequestBody.create(
                    MediaType.parse("application/json; charset=${charset.name()}"),
                    JSONObject(fieldMap).toString()
                )
            }
        } else {
            null
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

        LOG("urlBuilder: $urlBuilder")
        LOG("httpMethod: $httpMethod")

        return Request.Builder()
            .url(urlBuilder.toString())
            .method(endpoint.method, requestBody)
            .headers(headersBuilder.build())
            .build()
    }

    // =============== Path
    private fun String.replacePlaceholderAny(name: String, value: Any?, encoded: Boolean): String {
        return this.replace(
            "{$name}",
            when (value) {
                is String -> if (!encoded) URLEncoder.encode(value, charset.name()) else value
                null -> ""
                else -> if (!encoded) URLEncoder.encode("$value", charset.name()) else "$value"
            }
        )
    }

    // =============== Query
    private fun MutableMap<String, String>.toQueryString(): String {
        val queryString = StringBuilder()
        var first = true
        this.forEach {
            if (!first) queryString.append("&")
            queryString.append(it.key).append("=").append(it.value)
            first = false
        }
        return queryString.toString()
    }

    // =============== Query Field
    private fun MutableMap<String, String>.putAllAnyUrlEncode(
        value: Any?,
        needEncode: Boolean,
        encoded: Boolean,
        dateToString: DateToString
    ) {
        if (value is Map<*, *>) {
            value.forEach { entry ->
                entry.key?.let {
                    if (it is String) {
                        putAnyUrlEncode(it, entry.value, needEncode, encoded, dateToString)
                    }
                }
            }
        }
    }

    private fun MutableMap<String, String>.putAnyUrlEncode(
        name: String,
        value: Any?,
        needEncode: Boolean,
        encoded: Boolean,
        dateToString: DateToString
    ) {
        if ("" == name) return
        this[name] = when (value) {
            is String -> if (needEncode && !encoded) URLEncoder.encode(value, charset.name()) else value
            is Date -> if (needEncode && !encoded) URLEncoder.encode(
                dateToString(value),
                charset.name()
            ) else dateToString(
                value
            )
            null -> ""
            else -> if (needEncode && !encoded) URLEncoder.encode("$value", charset.name()) else "$value"
        }
    }

    // =============== Header
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
            value.entries.forEach { entry ->
                val name = entry.key
                if (name is String) {
                    addAny(name, entry.value)
                }
            }
        }
    }

}