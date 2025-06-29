package com.wutsi.koki.chatbot.telegram.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.chatbot.telegram.service.TelegramConsumer
import com.wutsi.koki.platform.logger.KVLogger
import org.mockito.Mockito.mock
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.test.Test

class WebhookControllerTest {
    private val consumer = mock<TelegramConsumer>()
    private val logger = mock<KVLogger>()
    private val webhook = WebhookController(consumer, logger)

    @Test
    fun onUpdate() {
        val update = mock<Update>()
        webhook.onUpdate(update)

        verify(consumer).consume(listOf(update), logger)
    }
}
