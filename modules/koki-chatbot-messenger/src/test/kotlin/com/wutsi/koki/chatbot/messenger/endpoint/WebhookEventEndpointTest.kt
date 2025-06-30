package com.wutsi.koki.chatbot.messenger.endpoint

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.Test

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebhookEventEndpointTest {
    @Test
    fun subscribe() {
    }
}
