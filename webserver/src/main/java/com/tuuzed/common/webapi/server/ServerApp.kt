package com.tuuzed.common.webapi.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class ServerApp

fun main(args: Array<String>) {
    SpringApplication.run(ServerApp::class.java, *args)
}