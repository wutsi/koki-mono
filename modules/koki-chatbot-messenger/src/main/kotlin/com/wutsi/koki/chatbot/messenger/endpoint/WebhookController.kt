package com.wutsi.koki.chatbot.messenger.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController {
    @PostMapping
    fun onMessageReceived(@RequestBody event: Map<String, Any>) {
        println(
            ">> Received" +
                ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(event)
        )
    }
}
