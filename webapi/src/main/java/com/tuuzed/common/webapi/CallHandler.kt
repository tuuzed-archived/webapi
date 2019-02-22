package com.tuuzed.common.webapi

import com.google.gson.Gson
import okhttp3.Call
import java.lang.reflect.Method

interface CallHandler {

    fun isSupported(
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>?
    ): Boolean

    fun handleCall(
        call: Call,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>?
    ): Any?

}