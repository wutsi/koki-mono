package com.wutsi.koki.chatbot.telegram.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale

@Service
class BotHandler(
    private val client: TelegramClient,
    private val messages: MessageSource
) : CommandHandler {
    companion object {
        const val ANSWER = "handler.bot"
    }

    override fun handle(update: Update) {
        if (update.message.from.isBot) {
            val locale = Locale(update.message.from.languageCode)
            val text = messages.getMessage(ANSWER, emptyArray(), locale)
            val msg = SendMessage(update.message.chatId.toString(), text)
            client.execute(msg)
        }
    }
}
