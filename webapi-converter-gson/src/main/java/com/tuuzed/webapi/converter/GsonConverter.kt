package com.tuuzed.webapi.converter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.tuuzed.webapi.Converter
import com.tuuzed.webapi.WebApiException
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonConverter(
    private val gson: Gson = GsonBuilder().create()
) : Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun <T> invoke(returnType: Type, response: Response): T {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val dataType = if (returnType is ParameterizedType) {
            returnType.ownerType
        } else {
            null
        }
        return when (dataType) {
            Response::class.java -> response as T
            ResponseBody::class.java -> response.body() as T
                ?: throw WebApiException.emptyResponseBody()
            InputStream::class.java -> response.body()?.byteStream() as T
                ?: throw WebApiException.emptyResponseBody()
            Reader::class.java -> response.body()?.charStream() as T
                ?: throw WebApiException.emptyResponseBody()
            String::class.java -> response.body()?.string() as T
                ?: throw WebApiException.emptyResponseBody()
            ByteArray::class.java -> response.body()?.bytes() as T
                ?: throw throw WebApiException.emptyResponseBody()
            BufferedSource::class.java -> response.body()?.source() as T
                ?: throw WebApiException.emptyResponseBody()
            null -> throw WebApiException.unsupportedReturnTypes()
            else -> try {
                gson.fromJson<T>(
                    response.body()?.charStream()
                        ?: throw WebApiException.emptyResponseBody(),
                    dataType
                )
            } catch (e: JsonParseException) {
                throw WebApiException.unsupportedReturnTypes(cause = e)
            } catch (e: Exception) {
                throw WebApiException.unknown(cause = e)
            }
        }

    }
}