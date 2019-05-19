package com.tuuzed.webapi.coroutines

import com.tuuzed.webapi.Call
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun <T> Call<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Call.Callback<T> {
            override fun onFailure(call: Call<T>, cause: IOException) {
                continuation.resumeWithException(cause)
            }

            override fun onResponse(call: Call<T>, t: T) {
                continuation.resume(t)
            }
        })
    }
}