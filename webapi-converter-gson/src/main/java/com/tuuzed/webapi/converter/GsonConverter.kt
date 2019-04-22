package com.tuuzed.webapi.converter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.tuuzed.webapi.Converter
import com.tuuzed.webapi.WebApiException
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonConverter(
    private val gson: Gson = GsonBuilder().create()
) : Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun <T> convert(returnType: Type, response: Response): T {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val dataType = if (returnType is ParameterizedType) {
            returnType.ownerType
        } else {
            null
        }
        return when (dataType) {
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