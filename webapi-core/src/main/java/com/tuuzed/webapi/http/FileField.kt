package com.tuuzed.webapi.http

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FileField(
    val name: String = "",
    val mediaType: String = "application/octet-stream"
)