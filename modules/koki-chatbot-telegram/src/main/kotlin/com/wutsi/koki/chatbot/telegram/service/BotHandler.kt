package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.tenant.TenantProvider
import jdk.jfr.internal.consumer.EventLog.update
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale

@Service
class HelpHandler(
    private val client: TelegramClient,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
) : CommandHandler{
    companion object {
        const val ANSWER_HELP = """
            Hello!

            I'm your assistant, and can help you to find properties for rent in {{country}}.

            You can control me with these commands:

            - `/search` - To search properties available for rental.
            - `/help` - To get help about this service
        """
    }

    override fun handle(update: Update) {
        val tenant = tenantService.tenant(tenantProvider.id() ?: -1)
        help(update, tenant)
    }

    private fun help(update: Update, tenant: TenantModel) {
        val language = update.message.from.languageCode
        val country = Locale(language, tenant.country).displayCountry

        val text = ANSWER_HELP.trimIndent().replace("{{country}}", country)
        sendText(text, update)
    }

    private fun sendText(text: String, update: Update) {
        val msg = SendMessage(update.message.chatId.toString(), text)
        msg.parseMode = ParseMode.MARKDOWN
        msg.enableMarkdown(true)
        client.execute(msg)
    }
}
