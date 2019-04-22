package com.tuuzed.webapi

import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type


class DefaultConverter : Converter {

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun <T> convert(returnType: Type, response: Response): T {
        throw WebApiException(WebApiException.CauseType.UNSUPPORTED_RETURN_TYPES)
    }
}