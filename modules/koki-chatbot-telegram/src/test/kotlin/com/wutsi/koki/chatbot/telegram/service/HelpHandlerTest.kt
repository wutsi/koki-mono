package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.chatbot.telegram.AbstractTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

class HelpHandlerTest : AbstractTest() {
    @MockitoBean
    private lateinit var client: TelegramClient

    @Autowired
    private lateinit var handler: HelpHandler

    @Test
    fun handle() {
        handler.handle(createUpdate("en"))

        val msg = argumentCaptor<SendMessage>()
        verify(client).execute(msg.capture())

        assertEquals(
            """
                Hello!
                I'm your assistant, and can help you to find properties for rent in Canada.
                You can control me with these commands:
                - `/search`: To search properties available for rental.
                - `/help`: To get help about this service
            """.trimIndent(),
            msg.firstValue.text,
        )
    }

    private fun createUpdate(language: String): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", false)
        update.message.from.languageCode = language
        update.message.chat = Chat(123, "channel")
        update.message.text = "yo"
        return update
    }
}
