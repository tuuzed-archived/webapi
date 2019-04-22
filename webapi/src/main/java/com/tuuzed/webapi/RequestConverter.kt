package com.tuuzed.webapi

import okhttp3.Request
import java.lang.reflect.Method

typealias RequestConverter = (webApiClazz: Class<*>, method: Method, args: Array<Any>?) -> Request