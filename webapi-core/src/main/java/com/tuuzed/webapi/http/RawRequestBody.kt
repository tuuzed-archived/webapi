package com.tuuzed.webapi.http

/**
 * 用于标注 RequestBody
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RawRequestBody