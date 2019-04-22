package com.tuuzed.webapi

import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DefaultCallAdapter : CallAdapter {

    override fun invoke(responseConverter: ResponseConverter, originalCall: okhttp3.Call): Call<*> {
        return CallImpl(responseConverter, originalCall)
    }

    private inner class CallImpl(
        private val responseConverter: ResponseConverter,
        private val originalCall: okhttp3.Call
    ) : Call<Any> {
        private val callImpl = this
        override fun request(): Request = originalCall.request()
        override fun cancel() = originalCall.cancel()
        override fun isExecuted(): Boolean = originalCall.isExecuted
        override fun isCanceled(): Boolean = originalCall.isCanceled
        override fun execute(): Any {
            return responseConverter(originalCall.execute())
        }

        override fun enqueue(callback: Call.Callback<Any>) = originalCall.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) = callback.onFailure(callImpl, e)
            override fun onResponse(call: okhttp3.Call, response: Response) {
                callback.onResponse(callImpl, responseConverter(response))
            }
        })
    }

}