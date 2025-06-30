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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
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
            // Loading...
            sendTextKey("chatbot.processing", update, locale)
            val response = chatbot.process(request)
            logger.add("room_ids", response.rooms.map { room -> room.id })
            logger.add("success", true)

            if (response.rooms.isNotEmpty()) {
                val urlBuilder = UrlBuilder(baseUrl = tenant.clientPortalUrl, medium = "telegram")

                // Rooms
                response.rooms.forEach { room ->
                    val title = toTitle(room, tenant, locale)
                    val url = urlBuilder.toPropertyUrl(room, request)

                    val text = title + "\n\n" + messages.getMessage("chatbot.link", arrayOf(url), locale)
                    sendTextKey(text, update, locale)
                }

                // View more
                if (response.searchParameters != null && response.searchLocation != null) {
                    val url = urlBuilder.toViewMoreUrl(response.searchParameters!!, request, response.searchLocation!!)
                    sendLinkKey(
                        "chatbot.location-rental",
                        "chatbot.find-more",
                        url,
                        update,
                        locale,
                        arrayOf(response.searchLocation!!.name)
                    )
                }

                // Track impression
                trackImpression(response.rooms, update)
            } else {
                sendTextKey("chatbot.not_found", update, locale)
            }
        } catch (ex: InvalidQueryException) {
            logger.add("success", false)
            logger.add("failure_reason", ex.message)
            sendTextKey("chatbot.help", update, locale, arrayOf(Locale(language, tenant.country).displayCountry))
        } catch (ex: Exception) {
            logger.add("success", false)
            logger.setException(ex)
            sendTextKey("chatbot.error", update, locale)
        }
    }

    private fun toTitle(property: RoomSummary, tenant: Tenant, locale: Locale): String {
        // Price
        val fmt = DecimalFormat(tenant.monetaryFormat)
        val price = property.pricePerMonth?.let { p ->
            messages.getMessage("chatbot.price-per-month", arrayOf(fmt.format(p.amount)), locale)
        } ?: property.pricePerNight?.let { p ->
            messages.getMessage("chatbot.price-per-night", arrayOf(fmt.format(p.amount)), locale)
        }

        // Bedroom
        val bedroom = messages.getMessage("chatbot.n-bedroom", arrayOf(property.numberOfRooms), locale)

        // bathroom
        val bathroom = if (property.numberOfBathrooms > 0) {
            messages.getMessage("chatbot.n-bathroom", arrayOf(property.numberOfBathrooms), locale)
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

    private fun sendTextKey(
        key: String,
        update: Update,
        locale: Locale,
        params: Array<Any> = emptyArray(),
    ) {
        val text = messages.getMessage(key, params, locale)
        return sendText(text, update)
    }

    private fun sendText(text: String, update: Update) {
        val msg = SendMessage.builder()
            .chatId(update.message.chatId.toString())
            .text(text)
            .build()
        telegram.execute(msg)
    }

    private fun sendLinkKey(
        textKey: String,
        urlKey: String,
        url: String,
        update: Update,
        locale: Locale,
        params: Array<Any> = emptyArray()
    ) {
        val msg = SendMessage.builder()
            .chatId(update.message.chatId.toString())
            .text(messages.getMessage(textKey, params, locale))
            .replyMarkup(
                InlineKeyboardMarkup.builder()
                    .keyboard(
                        listOf(
                            InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                    .url(url)
                                    .text(messages.getMessage(urlKey, params, locale))
                                    .build()
                            )
                        )
                    ).build()
            ).build()

        telegram.execute(msg)
    }
}
