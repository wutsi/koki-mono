package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale
import java.util.UUID
import kotlin.collections.forEach

@Service
class TelegramConsumer(
    private val telegram: TelegramClient,
    private val chatbot: Chatbot,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
    private val messages: MessageSource,
    private val publisher: Publisher,
) : LongPollingUpdateConsumer {
    companion object {
        val LOGGER = LoggerFactory.getLogger(TelegramConsumer::class.java)
    }

    override fun consume(updates: List<Update>) {
        updates.forEach { update ->
            consume(update)
        }
    }

    private fun consume(update: Update) {
        val chatId = update.message.chatId.toString()
        val tenant = tenantService.tenant(tenantProvider.id() ?: -1)
        val urlBuilder = UrlBuilder(
            baseUrl = tenant.clientPortalUrl,
            medium = "telegram"
        )
        val language = update.message.from.languageCode
        val request = ChatbotRequest(
            query = update.message.text,
            language = language,
            country = tenant.country
        )
        val locale = Locale(language)

        try {
            val response = chatbot.process(request)
            if (response.rooms.isNotEmpty()) {
                response.rooms.forEach { room ->
                    val title = toTitle(room, tenant, locale)
                    val url = urlBuilder.toPropertyUrl(room, request)

                    val text = title + "\n\n" + messages.getMessage("link", arrayOf(url), locale)
                    telegram.execute(SendMessage(chatId, text))
                }
                trackImpression(response.rooms, update)
            } else {
                telegram.execute(
                    SendMessage(
                        chatId,
                        messages.getMessage("chatbot.not_found", arrayOf(), locale),
                    )
                )
            }
        } catch (ex: InvalidQueryException) {
            LOGGER.warn("Invalid query", ex)
            telegram.execute(
                SendMessage(
                    chatId,
                    messages.getMessage(
                        "chatbot.help",
                        arrayOf(Locale(language, tenant.country).displayCountry),
                        locale
                    ),
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            telegram.execute(
                SendMessage(
                    chatId,
                    messages.getMessage("chatbot.error", arrayOf(), locale),
                )
            )
        }
    }

    private fun toTitle(property: RoomSummary, tenant: TenantModel, locale: Locale): String {
        // Price
        val fmt = tenant.createMoneyFormat()
        val price = property.pricePerMonth?.let { p ->
            messages.getMessage("price-per-month", arrayOf(fmt.format(p.amount)), locale)
        } ?: property.pricePerNight?.let { p ->
            messages.getMessage("price-per-night", arrayOf(fmt.format(p.amount)), locale)
        }

        // Bedroom
        val bedroom = messages.getMessage("n-bedroom", arrayOf(property.numberOfRooms), locale)

        // bathroom
        val bathroom = if (property.numberOfBathrooms > 0) {
            messages.getMessage("n-bathroom", arrayOf(property.numberOfBathrooms), locale)
        } else {
            null
        }

        return listOf(price, bedroom, bathroom).filterNotNull().joinToString(" | ")
    }

    private fun trackImpression(rooms: List<RoomSummary>, update: Update) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = System.currentTimeMillis(),
                    correlationId = UUID.randomUUID().toString(),
                    accountId = null,
                    tenantId = tenantProvider.id(),
                    productId = rooms.map { room -> room.id }.joinToString("|"),
                    deviceId = update.message.from.id.toString(),
                    event = TrackEvent.IMPRESSION,
                    page = "telegram",
                    channelType = ChannelType.MESSAGING,
                )
            )
        )
    }
}
