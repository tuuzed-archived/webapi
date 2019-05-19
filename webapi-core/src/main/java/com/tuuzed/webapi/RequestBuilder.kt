package com.tuuzed.webapi

import okhttp3.Request
import java.lang.reflect.Method

interface RequestBuilder {

    fun createRequest(webApiClazz: Class<*>, method: Method, args: Array<Any?>?): Request

}