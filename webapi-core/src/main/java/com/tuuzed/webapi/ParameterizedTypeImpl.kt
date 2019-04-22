package com.tuuzed.webapi

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(
    private val rawType: Type
) : ParameterizedType {

    override fun getRawType(): Type = rawType

    override fun getOwnerType(): Type? {
        return if (actualTypeArguments.isEmpty()) {
            null
        } else {
            actualTypeArguments[0]
        }
    }

    override fun getActualTypeArguments(): Array<Type> {
        return if (rawType is ParameterizedType) {
            rawType.actualTypeArguments
        } else {
            emptyArray()
        }
    }

}