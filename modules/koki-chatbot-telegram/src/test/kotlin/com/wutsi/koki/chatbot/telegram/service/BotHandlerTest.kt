package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.context.support.ResourceBundleMessageSource
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

class BotHandlerTest {
    private val client = mock<TelegramClient>()
    private val messages = ResourceBundleMessageSource()
    private val handler = BotHandler(client, messages)

    @BeforeEach
    fun setUp() {
        messages.setBasename("messages")
    }

    @Test
    fun bot() {
        handler.handle(createUpdate("yo", bot = true, language = "fr"))
        verify(client).execute(any<SendMessage>())
    }

    @Test
    fun `not bot`() {
        handler.handle(createUpdate("yo", bot = false))

        verify(client, never()).execute(any<SendMessage>())
    }

    private fun createUpdate(text: String, bot: Boolean = false, language: String = "en"): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", bot)
        update.message.from.languageCode = language
        update.message.chat = Chat(123, "channel")
        update.message.text = text
        return update
    }
}
