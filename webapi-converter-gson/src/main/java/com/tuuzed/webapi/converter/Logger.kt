package com.tuuzed.webapi.converter

import com.tuuzed.webapi.WebApiProxy

@Suppress("FunctionName")
internal fun LOG(msg: String) = WebApiProxy.logger?.invoke(msg)