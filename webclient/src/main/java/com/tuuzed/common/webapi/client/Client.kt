package com.tuuzed.common.webapi.client

import com.tuuzed.common.webapi.WebApiProxy


class Main {

    fun main() {
        val webApi = WebApiProxy(baseUrl = { "http://localhost:8080/api" }).create(WebApi::class.java)
        val obj = webApi.obj()
        println("obj: $obj")
        val list = webApi.list()
        println("list: $list")
        val error = webApi.error()
        println("error: $error")
    }

}

fun main() {
    Main().main()
}