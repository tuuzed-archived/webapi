package com.tuuzed.common.webapi

import com.google.gson.Gson
import okhttp3.Request
import java.lang.reflect.Method
import java.util.*

typealias  DateToString = (Date) -> String

interface RequestBuilder {

    fun buildRequest(
        baseUrl: String,
        dateToString: DateToString,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>?
    ): Request

}