package com.wutsi.koki.chatbot.messenger.endpoint

import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import java.io.IOException
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebhookSubscribeEndpointTest {
    private val verifyToken = System.getenv("MESSENGER_VERIFY_TOKEN")

    @LocalServerPort
    private lateinit var port: Integer

    @Test
    fun subscribe() {
        val text =
            URL("http://localhost:$port/webhook?hub.mode=subscribe&hub.challenge=123&hub.verify_token=$verifyToken").readText()

        assertEquals("123", text)
    }

    @Test
    fun `subscribe - invalid mode`() {
        val ex = assertThrows<IOException> {
            URL("http://localhost:$port/webhook?hub.mode=xxx&hub.challenge=123&hub.verify_token=$verifyToken").readText()
        }

        assertEquals(true, ex.message?.contains("Server returned HTTP response code: 403"))
    }

    @Test
    fun `subscribe - invalid token`() {
        val ex = assertThrows<IOException> {
            URL("http://localhost:$port/webhook?hub.mode=subscribe&hub.challenge=123&hub.verify_token=xxx").readText()
        }

        assertEquals(true, ex.message?.contains("Server returned HTTP response code: 403"))
    }
}
