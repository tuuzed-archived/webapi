package com.tuuzed.webapi

import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    fun <T> tryInvoke(returnType: Type, response: Response): T {
        val dataType = if (returnType is ParameterizedType) {
            returnType.ownerType
        } else {
            null
        }
        LOG("DefaultConverter: dataType=$dataType")
        return when (dataType) {
            Response::class.java -> response as T
            ResponseBody::class.java -> response.body() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            InputStream::class.java -> response.body()?.byteStream() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            Reader::class.java -> response.body()?.charStream() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            String::class.java -> response.body()?.string() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            ByteArray::class.java -> response.body()?.bytes() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            BufferedSource::class.java -> response.body()?.source() as T
                ?: throw WebApiException(WebApiException.CauseType.EMPTY_RESPONSE_BODY)
            null -> throw WebApiException.unsupportedReturnTypes()
            else -> return invoke(returnType, response)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    fun <T> invoke(returnType: Type, response: okhttp3.Response): T

}