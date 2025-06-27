package com.wutsi.koki.chatbot.telegram.service

import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale

@Service
class HelpHandler(
    private val client: TelegramClient,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
    private val messages: MessageSource,
) : CommandHandler {
    companion object {
        const val ANSWER = "handler.help"
    }

    override fun handle(update: Update) {
        val tenant = tenantService.tenant(tenantProvider.id() ?: -1)

        val language = update.message.from.languageCode
        val country = Locale(language, tenant.country).displayCountry
        val text = messages.getMessage(ANSWER, emptyArray(), Locale(language))
            .trimIndent()
            .replace("{{country}}", country)

        val msg = SendMessage(update.message.chatId.toString(), text)
        msg.enableMarkdown(true)
        client.execute(msg)
    }
}
