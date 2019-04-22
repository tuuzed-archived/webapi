package com.tuuzed.webapi.sample

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

fun main() {
    Vertx.vertx().deployVerticle(HttpServer::class.java.name)
}

class HttpServer : AbstractVerticle() {

    override fun start() {
        vertx.createHttpServer().requestHandler { req ->
            req.response().end(JsonObject().also {
                it.put("error", false)
                it.put("msg", req.uri())
            }.toString())
        }.listen(8080)
    }
}