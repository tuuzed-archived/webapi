package com.tuuzed.webapi

typealias Logger = (msg: String) -> Unit

@Suppress("FunctionName")
internal fun LOG(msg: String) = WebApiProxy.logger?.invoke(msg)