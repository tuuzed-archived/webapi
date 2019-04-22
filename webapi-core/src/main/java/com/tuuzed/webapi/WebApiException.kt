@file:Suppress("MemberVisibilityCanBePrivate")

package com.tuuzed.webapi

import java.io.IOException

class WebApiException(
    val causeType: CauseType,
    message: String = causeType.message,
    cause: Throwable? = null
) : IOException(message, cause) {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun network(message: String = CauseType.NETWORK.message, cause: Throwable? = null) =
            WebApiException(CauseType.NETWORK, message, cause)

        @JvmStatic
        @JvmOverloads
        fun emptyResponseBody(message: String = CauseType.EMPTY_RESPONSE_BODY.message, cause: Throwable? = null) =
            WebApiException(CauseType.EMPTY_RESPONSE_BODY, message, cause)


        @JvmStatic
        @JvmOverloads
        fun requestFailure(message: String = CauseType.REQUEST_FAILURE.message, cause: Throwable? = null) =
            WebApiException(CauseType.REQUEST_FAILURE, message, cause)

        @JvmStatic
        @JvmOverloads
        fun unsupportedReturnTypes(
            message: String = CauseType.UNSUPPORTED_RETURN_TYPES.message,
            cause: Throwable? = null
        ) = WebApiException(CauseType.UNSUPPORTED_RETURN_TYPES, message, cause)

        @JvmStatic
        @JvmOverloads
        fun unknown(message: String = CauseType.UNKNOWN.message, cause: Throwable? = null) =
            WebApiException(CauseType.UNKNOWN, message, cause)


    }

    enum class CauseType(val message: String) {
        NETWORK("网络连接异常"),
        EMPTY_RESPONSE_BODY("服务器未响应内容"),
        REQUEST_FAILURE("请求数据失败"),
        UNSUPPORTED_RETURN_TYPES("不支持的返回类型"),
        UNKNOWN("连接服务器失败，未知错误")
    }

    override fun toString(): String {
        return "WebApiException,causeType=$causeType"
    }

}