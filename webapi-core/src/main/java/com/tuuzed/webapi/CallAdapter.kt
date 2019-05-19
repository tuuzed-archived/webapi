package com.tuuzed.webapi

import java.lang.reflect.Method


interface CallAdapter<T> {

    fun call(method: Method, args: Array<Any?>?, converter: Converter, originalCall: okhttp3.Call): T


}