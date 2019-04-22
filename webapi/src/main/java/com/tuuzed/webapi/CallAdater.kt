package com.tuuzed.webapi

typealias CallAdapter = (responseConverter: ResponseConverter, originalCall: okhttp3.Call) -> Call<*>
