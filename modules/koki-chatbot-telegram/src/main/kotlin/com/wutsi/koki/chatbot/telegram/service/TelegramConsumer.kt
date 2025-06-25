package com.wutsi.koki.chatbot.telegram.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.collections.forEach

@Service
class TelegramConsumer(
    private val helpHandler: HelpHandler,
    private val searchHandler: SearchHandler,
    private val botHandler: BotHandler,
) : LongPollingUpdateConsumer {
    override fun consume(updates: List<Update>) {
        updates.forEach { update ->
            consume(update)
        }
    }

    private fun consume(update: Update) {
        if (update.message.from.isBot) {
            botHandler.handle(update)
        } else {
            if (update.message.isCommand) {
                when (update.message.entities[0].text) {
                    "/search" -> searchHandler.handle(update)
                    else -> helpHandler.handle(update)
                }
            } else {
                helpHandler.handle(update)
            }
        }
    }
}
