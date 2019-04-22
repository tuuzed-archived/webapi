package com.tuuzed.webapi.http

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(
    val name: String,
    // URL encoded
    val encoded: Boolean = false
)