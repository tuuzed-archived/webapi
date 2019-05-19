package com.tuuzed.webapi

import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Method

object ConverterUtils {

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @Throws(IOException::class)
    fun <T> tryConvert(converter: Converter, method: Method, args: Array<Any?>?, response: Response): T {
        val returnType = ParameterizedTypeImpl(method.genericReturnType)
        return when (val dataType = returnType.ownerType) {
            Response::class.java -> response as T
            ResponseBody::class.java -> (response.body() ?: throw WebApiException.emptyResponseBody()) as T
            InputStream::class.java -> response.body()?.byteStream() as T ?: throw WebApiException.emptyResponseBody()
            Reader::class.java -> response.body()?.charStream() as T ?: throw WebApiException.emptyResponseBody()
            String::class.java -> response.body()?.string() as T ?: throw WebApiException.emptyResponseBody()
            ByteArray::class.java -> response.body()?.bytes() as T ?: throw WebApiException.emptyResponseBody()
            BufferedSource::class.java -> response.body()?.source() as T ?: throw WebApiException.emptyResponseBody()
            null -> throw WebApiException.unsupportedReturnTypes()
            else -> return converter.convert(dataType, args, response)
        }
    }
}