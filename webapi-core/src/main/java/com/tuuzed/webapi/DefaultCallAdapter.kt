package com.tuuzed.webapi

import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Method

class DefaultCallAdapter : CallAdapter<Call<*>> {

    override fun invoke(method: Method, args: Array<Any?>?, converter: Converter, okHttpCall: okhttp3.Call): Call<*> {
        return CallImpl(method, args, converter, okHttpCall)
    }

    private class CallImpl(
        private val method: Method,
        private val args: Array<Any?>?,
        private val converter: Converter,
        private val okHttpCall: okhttp3.Call
    ) : Call<Any> {
        private val callImpl = this
        override fun request(): Request = okHttpCall.request()
        override fun cancel() = okHttpCall.cancel()
        override fun isExecuted(): Boolean = okHttpCall.isExecuted
        override fun isCanceled(): Boolean = okHttpCall.isCanceled

        @Throws(IOException::class)
        override fun execute(): Any {
            val response = okHttpCall.execute()
            return converter.tryInvoke(method, args, response)
        }

        override fun enqueue(callback: Call.Callback<Any>) = okHttpCall.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) = callback.onFailure(callImpl, e)
            override fun onResponse(call: okhttp3.Call, response: Response) {
                callback.onResponse(
                    callImpl,
                    converter.tryInvoke(method, args, response)
                )
            }
        })

    }

}