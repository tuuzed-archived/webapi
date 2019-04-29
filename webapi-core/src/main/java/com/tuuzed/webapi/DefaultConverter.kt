package com.tuuzed.webapi

import java.io.IOException
import java.lang.reflect.Type


class DefaultConverter : Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun <T> invoke(dataType: Type, args: Array<Any?>?, response: okhttp3.Response): T {
        throw WebApiException(WebApiException.CauseType.UNSUPPORTED_RETURN_TYPES)
    }
}