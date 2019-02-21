package com.tuuzed.common.webapi

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * 支持的返回类型
 * @see Resp
 * @see ListResp
 */
class DefaultCallHandler : CallHandler {

    override fun handleCall(
        call: Call,
        gson: Gson,
        webApiClass: Class<*>,
        method: Method,
        args: Array<out Any?>
    ): Any? {
        try {
            val response = call.execute()
            val body =
                response.body() ?: return createErrorRst(method, WebApiException(WebApiException.Type.EMPTY_BODY))
            val content = body.string()
            val resp = gson.fromJson<Any>(content, method.genericReturnType)
            when (resp) {
                is Resp<*> -> {
                    if (resp.error) {
                        if (resp.msg == null) {
                            resp.msg = WebApiException.Type.REQ_FAILURE.message
                            resp.tr = WebApiException(WebApiException.Type.REQ_FAILURE, "${resp.msg}")
                        } else {
                            resp.tr = WebApiException(WebApiException.Type.REQ_FAILURE, "${resp.msg}")
                        }
                    }
                }
                is ListResp<*> -> {
                    if (resp.error) {
                        if (resp.msg == null) {
                            resp.msg = WebApiException.Type.REQ_FAILURE.message
                            resp.tr = WebApiException(WebApiException.Type.REQ_FAILURE, "${resp.msg}")
                        } else {
                            resp.tr = WebApiException(WebApiException.Type.REQ_FAILURE, "${resp.msg}")
                        }
                    }
                }
                else -> {
                    throw RuntimeException("不支持的返回类型（${method.genericReturnType}）。")
                }
            }
            return resp
        } catch (ex: IOException) {
            return createErrorRst(method, WebApiException(WebApiException.Type.NETWORK, ex))
        } catch (ex: JsonSyntaxException) {
            return createErrorRst(method, WebApiException(WebApiException.Type.JSON_FORMAT, ex))
        } catch (tr: Throwable) {
            return createErrorRst(method, WebApiException(WebApiException.Type.UNKNOWN, tr))
        }
    }

    private fun createErrorRst(method: Method, tr: WebApiException, msg: String = tr.type.message): Any {
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            return when (genericReturnType.rawType) {
                Resp::class.java -> Resp<Any>(error = true, tr = tr, msg = msg)
                ListResp::class.java -> ListResp<Any>(error = true, tr = tr, msg = msg)
                else -> throw RuntimeException("genericReturnType 错误")
            }
        }
        throw RuntimeException("genericReturnType 错误")
    }


}