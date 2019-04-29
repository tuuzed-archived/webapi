package com.tuuzed.webapi.adapter

import com.tuuzed.webapi.CallAdapter
import com.tuuzed.webapi.Converter
import com.tuuzed.webapi.OkHttpCall
import com.tuuzed.webapi.ParameterizedTypeImpl
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import java.lang.reflect.Method

class RxJava2CallAdapter : CallAdapter<Flowable<*>> {

    override fun invoke(method: Method, converter: Converter, originalCall: OkHttpCall): Flowable<*> {
        return Flowable.create(FlowableOnSubscribe<Any> {
            val response = originalCall.execute()
            it.onNext(
                converter.tryInvoke(
                    ParameterizedTypeImpl(method.genericReturnType),
                    response
                )
            )
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

}