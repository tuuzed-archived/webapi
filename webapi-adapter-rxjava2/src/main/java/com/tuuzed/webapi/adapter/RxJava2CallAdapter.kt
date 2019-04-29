package com.tuuzed.webapi.adapter

import com.tuuzed.webapi.CallAdapter
import com.tuuzed.webapi.Converter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import java.lang.reflect.Method

class RxJava2CallAdapter : CallAdapter<Flowable<*>> {

    override fun invoke(
        method: Method,
        args: Array<Any?>?,
        converter: Converter,
        okHttpCall: okhttp3.Call
    ): Flowable<*> {
        return Flowable.create(FlowableOnSubscribe<Any> {
            val response = okHttpCall.execute()
            it.onNext(
                converter.tryInvoke(method, args, response)
            )
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

}