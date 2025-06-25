package com.wutsi.koki.chatbot.telegram.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.mockito.Mockito.mock
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import kotlin.test.Test

class TelegramConsumerTest {
    private val helpHandler = mock<HelpHandler>()
    private val searchHandler = mock<SearchHandler>()
    private val botHandler = mock<BotHandler>()

    private val consumer = TelegramConsumer(
        botHandler = botHandler,
        helpHandler = helpHandler,
        searchHandler = searchHandler,
    )

    @Test
    fun bot() {
        val update = createUpdate("yo", bot = true)
        consumer.consume(listOf(update))

        verify(helpHandler, never()).handle(any())
        verify(searchHandler, never()).handle(any())
        verify(botHandler).handle(update)
    }

    @Test
    fun text() {
        val update = createUpdate("yo")
        consumer.consume(listOf(update))

        verify(helpHandler).handle(update)
        verify(searchHandler, never()).handle(any())
        verify(botHandler, never()).handle(any())
    }

    @Test
    fun help() {
        val update = createUpdate("/help")
        consumer.consume(listOf(update))

        verify(helpHandler).handle(update)
        verify(searchHandler, never()).handle(any())
        verify(botHandler, never()).handle(any())
    }

    @Test
    fun search() {
        val update = createUpdate("/search Im looking for apartment in Yaounde")
        consumer.consume(listOf(update))

        verify(helpHandler, never()).handle(any())
        verify(searchHandler).handle(update)
        verify(botHandler, never()).handle(any())
    }

    private fun createUpdate(text: String, bot: Boolean = false, language: String = "en"): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", bot)
        update.message.from.languageCode = language
        update.message.chat = Chat(123, "channel")
        update.message.text = text

        if (text.startsWith("/")) {
            val i = text.indexOf(" ")
            val cmd = if (i > 0) text.substring(0, i) else text
            val msg = MessageEntity("bot_command", 0, cmd.length)
            msg.text = cmd
            update.message.entities = listOf(msg)
        }
        return update
    }
}
