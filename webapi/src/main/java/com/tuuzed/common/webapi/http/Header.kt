package com.tuuzed.common.webapi.http

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Header(
    val name: String = "",
    val line: String = "" // name:value
)