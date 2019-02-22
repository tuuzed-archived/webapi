package com.tuuzed.common.webapi.server

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {

    @RequestMapping("/api/error")
    fun error(): Map<String, Any> {
        val resp = HashMap<String, Any>()
        resp["error"] = true
        resp["msg"] = "Error"
        val payload = HashMap<String, Any>()
        payload.putAll(resp)
        resp["payload"] = payload
        return resp
    }

    @RequestMapping("/api/obj")
    fun obj(): Map<String, Any> {
        val resp = HashMap<String, Any>()
        resp["error"] = false
        resp["msg"] = "Success"
        val payload = HashMap<String, Any>()
        payload.putAll(resp)
        resp["payload"] = payload
        return resp
    }

    @RequestMapping("/api/list")
    fun list(): Map<String, Any> {
        val resp = HashMap<String, Any>()
        resp["error"] = false
        resp["msg"] = "Success"
        val payload = listOf(resp.clone())
        resp["payload"] = payload
        return resp
    }


}