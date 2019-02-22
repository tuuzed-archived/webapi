package com.tuuzed.common.webapi

import com.google.gson.Gson
import okhttp3.Request
import java.lang.reflect.Method

interface RequestBuilder {

    fun buildRequest(
        baseUrl: String,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>?
    ): Request

}