package com.tuuzed.common.webapi

import com.google.gson.Gson
import okhttp3.Call
import java.lang.reflect.Method

interface CallHandler {

    fun handleCall(
        call: Call,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>
    ): Any?

}