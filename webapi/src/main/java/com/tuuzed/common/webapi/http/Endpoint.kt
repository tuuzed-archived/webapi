package com.tuuzed.common.webapi.http

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Endpoint(
    val value: String,
    val method: String
)