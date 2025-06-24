package com.wutsi.koki.chatbot.telegram.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.url.UrlShortener
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale
import java.util.concurrent.ExecutorService
import kotlin.collections.forEach

class TelegramConsumer(
    private val client: TelegramClient,
    private val agentFactory: AgentFactory,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
    private val objectMapper: ObjectMapper,
    private val executorService: ExecutorService,
    private val urlShortener: UrlShortener,
) : LongPollingUpdateConsumer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TelegramConsumer::class.java)

        const val ANSWER_BOT = "Sorry! I don't talk to bot!"
        const val ANSWER_HELP = """
            Hello!

            I'm your assistant, and can help you to find properties for rent in {{country}}.

            You can control me with these commands:

            - `/search` - To search properties available for rental.
            - `/help` - To get help about this service
        """
        const val ANSWER_SEARCHING = "Searching..."
        const val ANSWER_FAILURE = "Oops... An unexpected error occurred. Please try again"
        const val ANSWER_NOT_FOUND = "No properties found"
    }

    override fun consume(updates: List<Update>) {
        updates.forEach { update ->
            val runnable = Runnable {
                consume(update)
            }
            executorService.execute(runnable)
        }
    }

    private fun consume(update: Update) {
        try {
            if (ignore(update)) {
                sendText(ANSWER_BOT, update)
            } else {
                val tenant = tenantService.tenant(tenantProvider.id() ?: -1)
                if (update.message.isCommand) {
                    when (update.message.entities[0].text) {
                        "/search" -> search(update.message.text.substring(7).trim(), update, tenant)
                        else -> help(update, tenant)
                    }
                } else {
                    help(update, tenant)
                }
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            sendText(ANSWER_FAILURE, update)
        }
    }

    private fun help(update: Update, tenant: TenantModel) {
        val language = update.message.from.languageCode
        val country = Locale(language, tenant.country).displayCountry

        val text = ANSWER_HELP.trimIndent().replace("{{country}}", country)
        sendText(text, update)
    }

    private fun search(query: String, update: Update, tenant: TenantModel) {
        sendText(ANSWER_SEARCHING, update)

        val agent = agentFactory.crateSearchAgent()
        val json = agent.run(query)
        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        if (result.properties.isEmpty()) {
            sendText(ANSWER_NOT_FOUND, update)
        } else {
            val language = update.message.from.languageCode
            val fmt = tenant.createMoneyFormat()
            result.properties.map { property ->
                // Price
                val price = if (property.pricePerMonth != null) {
                    fmt.format(property.pricePerMonth) + "/month"
                } else {
                    fmt.format(property.pricePerNight) + "/night"
                }

                // Bedroom
                val bedroom = "${property.bedrooms} BR"

                // bathroom
                val bathroom = if (property.bathrooms > 0) {
                    "${property.bathrooms} BA"
                } else {
                    null
                }
                val title = listOf(price, bedroom, bathroom).filterNotNull().joinToString(" | ")

                // Location
                val location = listOf(property.neighborhood, property.city).filterNotNull().joinToString(", ")

                // URL
                val url = urlShortener.shorten(
                    "${tenant.clientPortalUrl}${property.url}?lang=$language&utm-medium=telegram"
                )

                sendText("$title\n$location\n\n$url", update)
            }
        }
    }

    private fun ignore(update: Update): Boolean {
        return update.message.from.isBot
    }

    private fun sendText(text: String, update: Update) {
        val msg = SendMessage(update.message.chatId.toString(), text)
        msg.parseMode = ParseMode.MARKDOWN
        msg.enableMarkdown(true)
        client.execute(msg)
    }
}
