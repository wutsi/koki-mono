package com.wutsi.koki.chatbot.telegram.endpoint

import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.objects.Update

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val consumer: TelegramConsumer,
    private val logger: KVLogger,
) {
    @PostMapping
    fun onUpdate(@RequestBody update: Update) {
        consumer.consume(listOf(update), logger)
    }
}
