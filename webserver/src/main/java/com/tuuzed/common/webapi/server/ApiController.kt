package com.tuuzed.common.webapi.server

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {
    @RequestMapping("/", method = [RequestMethod.GET])
    fun index(): String {
        return "TODO"
    }
}