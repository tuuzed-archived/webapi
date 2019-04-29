package com.tuuzed.webapi

import java.lang.reflect.Method


interface CallAdapter<T> {

    fun invoke(method: Method, converter: Converter, okHttpCall: OkHttpCall): T


}