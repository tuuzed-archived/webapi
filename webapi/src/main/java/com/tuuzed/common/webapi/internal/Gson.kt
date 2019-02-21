package com.tuuzed.common.webapi.internal

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class IgnoreAnnotationExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?) = false
    override fun shouldSkipField(f: FieldAttributes?) = f?.getAnnotation(WebApiIgnore::class.java) != null
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebApiIgnore

