package com.tuuzed.webapi

import okhttp3.Request
import java.io.IOException

interface Call<T> {
    fun request(): Request
    fun cancel()
    fun isExecuted(): Boolean
    fun isCanceled(): Boolean

    @Throws(IOException::class)
    fun execute(): T

    fun enqueue(callback: Callback<T>)

    interface Callback<T> {
        fun onFailure(call: Call<T>, cause: IOException)
        fun onResponse(call: Call<T>, t: T)
    }

}

