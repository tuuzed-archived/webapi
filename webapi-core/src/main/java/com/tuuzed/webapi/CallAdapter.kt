package com.tuuzed.webapi

import java.lang.reflect.Method


interface CallAdapter {

    fun invoke(
        method: Method,
        converter: Converter,
        originalCall: OkHttpCall
    ): Call<*>

}