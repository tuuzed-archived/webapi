package com.tuuzed.common.webapi.http

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Query(
    val name: String,
    val encoded: Boolean = false
)