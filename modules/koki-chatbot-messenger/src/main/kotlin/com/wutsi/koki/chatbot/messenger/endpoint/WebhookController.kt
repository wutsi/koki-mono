package com.wutsi.koki.chatbot.messenger.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController(
    @Value("\${koki.messenger.verify-token}") private val verifyToken: String
) {
    @PostMapping
    fun onMessageReceived(@RequestBody event: Map<String, Any>) {
        println(
            ">> Received" +
                ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(event)
        )
    }

    @GetMapping(produces = ["text/plain"])
    fun subscribe(
        @RequestParam(name = "hub.mode") mode: String,
        @RequestParam(name = "hub.challenge") challenge: String,
        @RequestParam(name = "hub.verify_token") token: String,
        response: HttpServletResponse,
    ): String {
        if (mode == "subscribe" && token == verifyToken) {
            return challenge
        } else {
            response.sendError(403)
            return "Failed"
        }
    }
}
