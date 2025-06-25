package com.wutsi.koki.chatbot.telegram.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

@Service
class BotHandler(
    private val client: TelegramClient,
) : CommandHandler {
    companion object {
        const val ANSWER = "Sorry! I don't talk to bot!"
    }

    override fun handle(update: Update) {
        if (update.message.from.isBot) {
            client.execute(SendMessage(update.message.chatId.toString(), ANSWER))
        }
    }
}
