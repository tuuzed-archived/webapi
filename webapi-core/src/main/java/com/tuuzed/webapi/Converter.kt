package com.tuuzed.webapi

import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

interface Converter {

    @Throws(IOException::class)
    fun <T> invoke(returnType: Type, response: Response): T

}