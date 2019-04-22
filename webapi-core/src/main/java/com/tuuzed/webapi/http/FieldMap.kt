package com.tuuzed.webapi.http

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldMap(
    val encoded: Boolean = false
)