package com.tuuzed.common.webapi.http

@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryMap(
    val encoded: Boolean = false
)