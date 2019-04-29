package com.tuuzed.webapi.adapter

import com.tuuzed.webapi.WebApiProxy

@Suppress("FunctionName")
internal fun LOG(msg: String) = WebApiProxy.logger?.invoke(msg)