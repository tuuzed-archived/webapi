package com.tuuzed.webapi.adapter

import com.tuuzed.webapi.CallAdapter
import com.tuuzed.webapi.Converter
import com.tuuzed.webapi.ConverterUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Method

class RxJava2CallAdapter : CallAdapter<Flowable<*>> {

    override fun call(method: Method, args: Array<Any?>?, converter: Converter, originalCall: Call): Flowable<*> {
        return Flowable.create(FlowableOnSubscribe<Any> {
            originalCall.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    it.onNext(ConverterUtils.tryConvert(converter, method, args, response))
                    it.onComplete()
                }
            })
        }, BackpressureStrategy.BUFFER).doOnCancel {
            originalCall.cancel()
        }
    }

}