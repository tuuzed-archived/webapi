package com.tuuzed.webapi

import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

interface Converter {

    @Throws(IOException::class)
    fun <T> convert(dataType: Type, args: Array<Any?>?, response: Response): T

}