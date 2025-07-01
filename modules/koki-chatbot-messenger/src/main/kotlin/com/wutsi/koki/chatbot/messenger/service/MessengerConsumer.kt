package com.wutsi.koki.chatbot.messenger.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.UrlBuilder
import com.wutsi.koki.chatbot.messenger.model.Messaging
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.UUID

@Service
class MessengerConsumer(
    private val messenger: MessengerClient,
    private val chatbot: Chatbot,
    private val tenantProvider: TenantProvider,
    private val kokiTenant: KokiTenants,
    private val messages: MessageSource,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    fun consume(messaging: Messaging) {
        logger.add("messaging_sender_id", messaging.sender.id)
        logger.add("messaging_recipient_id", messaging.recipient.id)
        logger.add("messaging_message_text", messaging.message?.text)

        if (accept(messaging)) {
            doConsume(messaging)
        }
    }

    private fun accept(messaging: Messaging): Boolean {
        if (messaging.message?.text.isNullOrEmpty()) {
            logger.add("success", false)
            logger.add("failure_reason", "no_text")
            return false
        }
        return true
    }

    private fun doConsume(messaging: Messaging) {
        val tenantId = tenantProvider.id()
        val tenant = kokiTenant.tenant(tenantId ?: -1).tenant
        logger.add("tenant_id", tenantId)

        val language = "en"
        val request = ChatbotRequest(
            query = messaging.message?.text ?: "",
            language = language,
            country = tenant.country
        )
        val locale = Locale(language)

        try {
            // Loading...
            sendTextKey("chatbot.processing", messaging, locale)
            val response = chatbot.process(request)
            logger.add("room_ids", response.rooms.map { room -> room.id })
            logger.add("success", true)

            if (response.rooms.isNotEmpty()) {
                val urlBuilder = UrlBuilder(baseUrl = tenant.clientPortalUrl, medium = "messenger")

                // Rooms
                response.rooms.forEach { room ->
                    val url = urlBuilder.toPropertyUrl(room, request)
                    sendText(url, messaging)
                }

                // View more
                // TODO

                // Track impression
                trackImpression(response.rooms, messaging)
            } else {
                sendTextKey("chatbot.not_found", messaging, locale)
            }
        } catch (ex: InvalidQueryException) {
            logger.add("success", false)
            logger.add("failure_reason", ex.message)
            sendTextKey("chatbot.help", messaging, locale, arrayOf(Locale(language, tenant.country).displayCountry))
        } catch (ex: Exception) {
            logger.add("success", false)
            logger.setException(ex)
            sendTextKey("chatbot.error", messaging, locale)
        }
    }

    private fun sendTextKey(key: String, messaging: Messaging, locale: Locale, params: Array<Any> = emptyArray()) {
        val text = messages.getMessage(key, params, locale)
        sendText(text, messaging)
    }

    private fun sendText(text: String, messaging: Messaging) {
        messenger.send(
            recipientId = messaging.sender.id,
            text = text
        )
    }

    private fun trackImpression(rooms: List<RoomSummary>, messaging: Messaging) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = messaging.timestamp,
                    correlationId = UUID.randomUUID().toString(),
                    accountId = null,
                    tenantId = tenantProvider.id(),
                    productId = rooms.map { room -> room.id }.joinToString("|"),
                    deviceId = messaging.sender.id,
                    event = TrackEvent.IMPRESSION,
                    page = "messenger",
                    channelType = ChannelType.MESSAGING,
                )
            )
        )
    }
}
