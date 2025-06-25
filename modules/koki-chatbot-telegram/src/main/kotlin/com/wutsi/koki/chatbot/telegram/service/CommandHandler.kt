package com.wutsi.koki.chatbot.telegram.service

import org.telegram.telegrambots.meta.api.objects.Update

interface CommandHandler {
    fun handle(update: Update)
}
