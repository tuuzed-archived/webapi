package com.tuuzed.webapi

import java.lang.reflect.Method


interface CallAdapter<T> {

    fun invoke(method: Method, converter: Converter, okHttpCall: okhttp3.Call): T


}