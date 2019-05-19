package com.tuuzed.webapi

import java.io.IOException
import java.lang.reflect.Type


class DefaultConverter : Converter {

    @Throws(IOException::class)
    override fun <T> convert(dataType: Type, args: Array<Any?>?, response: okhttp3.Response): T {
        throw WebApiException.unsupportedReturnTypes()
    }

}