package com.tuuzed.common.webapi.http

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(
    val name: String,
    // URL encode
    val encoded: Boolean = false
)