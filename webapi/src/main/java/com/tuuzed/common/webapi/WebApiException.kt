package com.tuuzed.common.webapi


class WebApiException @JvmOverloads constructor(
    val type: Type,
    message: String = type.message,
    cause: Throwable? = null
) : Exception(message, cause) {

    enum class Type(val message: String) {
        NETWORK("网络连接异常"),
        EMPTY_BODY("服务器未响应内容"),
        REQ_FAILURE("请求数据失败"),
        JSON_FORMAT("服务器返回数据格式错误"),
        UNKNOWN("连接服务器失败，未知错误")
    }

    constructor(type: Type, cause: Throwable?) : this(type, type.message, cause)
}
