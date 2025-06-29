package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.UrlBuilder
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.text.DecimalFormat
import java.util.Locale
import java.util.UUID
import kotlin.collections.forEach

@Service
class TelegramConsumer(
    private val telegram: TelegramClient,
    private val chatbot: Chatbot,
    private val tenantProvider: TenantProvider,
    private val kokiTenant: KokiTenants,
    private val messages: MessageSource,
    private val publisher: Publisher,
) : LongPollingUpdateConsumer {
    override fun consume(updates: List<Update>) {
        val logger = DefaultKVLogger()
        consume(updates, logger)
    }

    fun consume(updates: List<Update>, logger: KVLogger) {
        updates.forEach { update ->
            logger.add("update_message_chat_id", update.message.chat.id)
            logger.add("update_message_chat_name", update.message.chat.userName)
            logger.add("update_message_from_language", update.message.from.languageCode)
            logger.add("update_message_text", update.message.text)

            try {
                if (accept(update, logger)) {
                    consume(update, logger)
                }
            } finally {
                logger.log()
            }
        }
    }

    private fun accept(update: Update, logger: KVLogger): Boolean {
        if (update.message.text.isNullOrEmpty()) {
            logger.add("success", false)
            logger.add("failure_reason", "no_text")
            return false
        }
        return true
    }

    private fun consume(update: Update, logger: KVLogger) {
        val chatId = update.message.chatId.toString()

        val tenantId = tenantProvider.id()
        val tenant = kokiTenant.tenant(tenantId ?: -1).tenant
        logger.add("tenant_id", tenantId)

        val language = update.message.from.languageCode
        val request = ChatbotRequest(
            query = update.message.text,
            language = language,
            country = tenant.country
        )
        val locale = Locale(language)

        try {
            val response = chatbot.process(request)
            logger.add("room_ids", response.rooms.map { room -> room.id })
            logger.add("success", true)

            if (response.rooms.isNotEmpty()) {
                val urlBuilder = UrlBuilder(baseUrl = tenant.clientPortalUrl, medium = "telegram")
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
            logger.add("success", false)
            logger.add("failure_reason", ex.message)

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
            logger.add("success", false)
            logger.setException(ex)
            telegram.execute(
                SendMessage(
                    chatId,
                    messages.getMessage("chatbot.error", arrayOf(), locale),
                )
            )
        }
    }

    private fun toTitle(property: RoomSummary, tenant: Tenant, locale: Locale): String {
        // Price
        val fmt = DecimalFormat(tenant.monetaryFormat)
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
