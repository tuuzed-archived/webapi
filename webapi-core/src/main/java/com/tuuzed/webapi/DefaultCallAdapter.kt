package com.tuuzed.webapi

import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Method

class DefaultCallAdapter : CallAdapter {

    override fun invoke(
        method: Method,
        converter: Converter,
        originalCall: OriginalCall
    ): Call<*> {
        return CallImpl(method, converter, originalCall)
    }

    private class CallImpl(
        private val method: Method,
        private val converter: Converter,
        private val originalCall: OriginalCall
    ) : Call<Any> {
        private val callImpl = this
        override fun request(): Request = originalCall.request()
        override fun cancel() = originalCall.cancel()
        override fun isExecuted(): Boolean = originalCall.isExecuted
        override fun isCanceled(): Boolean = originalCall.isCanceled
        override fun execute(): Any {
            return converter.invoke(
                ParameterizedTypeImpl(method.genericReturnType),
                originalCall.execute()
            )
        }

        override fun enqueue(callback: Call.Callback<Any>) = originalCall.enqueue(object : Callback {
            override fun onFailure(call: OriginalCall, e: IOException) = callback.onFailure(callImpl, e)
            override fun onResponse(call: OriginalCall, response: Response) {
                callback.onResponse(
                    callImpl,
                    converter.invoke(
                        ParameterizedTypeImpl(method.genericReturnType),
                        response
                    )
                )
            }
        })
    }

}