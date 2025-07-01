package com.wutsi.koki.chatbot.messenger.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.UrlBuilder
import com.wutsi.koki.chatbot.messenger.model.Attachment
import com.wutsi.koki.chatbot.messenger.model.Button
import com.wutsi.koki.chatbot.messenger.model.Element
import com.wutsi.koki.chatbot.messenger.model.Message
import com.wutsi.koki.chatbot.messenger.model.Messaging
import com.wutsi.koki.chatbot.messenger.model.Party
import com.wutsi.koki.chatbot.messenger.model.Payload
import com.wutsi.koki.chatbot.messenger.model.SendRequest
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.util.Locale
import java.util.UUID

@Service
class MessengerConsumer(
    private val messenger: MessengerClient,
    private val chatbot: Chatbot,
    private val tenantProvider: TenantProvider,
    private val kokiTenant: KokiTenants,
    private val kokiFiles: KokiFiles,
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

                // Images
                val imageIds = response.rooms.mapNotNull { room -> room.heroImageId }
                logger.add("image_ids", imageIds)

                val images = if (imageIds.isEmpty()) {
                    kokiFiles.files(
                        ids = imageIds,
                        limit = imageIds.size,
                        offset = 0,
                        type = FileType.IMAGE,
                        status = FileStatus.APPROVED,
                        ownerId = null,
                        ownerType = null,
                    ).files.associateBy { file -> file.id }
                } else {
                    emptyMap()
                }

                // Rooms
                sendProperties(response.rooms, images, request, tenant, locale, urlBuilder, messaging)

                // View more
                // TODO

                // Track impression
                trackImpression(response.rooms, messaging)
            } else {
                sendTextKey("chatbot.not-found", messaging, locale)
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

    /**
     * See https://developers.facebook.com/docs/messenger-platform/send-messages/template/generic
     */
    private fun sendProperties(
        rooms: List<RoomSummary>,
        images: Map<Long, FileSummary>,
        request: ChatbotRequest,
        tenant: Tenant,
        locale: Locale,
        urlBuilder: UrlBuilder,
        messaging: Messaging,
    ) {
        val request = SendRequest(
            recipient = Party(messaging.sender.id),
            message = Message(
                attachment = Attachment(
                    type = "template",
                    payload = Payload(
                        template_type = "generic",
                        elements = rooms.map { room ->
                            Element(
                                title = if (locale.language == "en") room.title else room.titleFr,
                                subtitle = toSubTitle(room, tenant, locale),
                                imageUrl = room.heroImageId?.let { id -> images[id]?.url },
                                default_action = Button(
                                    type = "web_url",
                                    url = urlBuilder.toPropertyUrl(room, request),
                                ),
                                buttons = listOf(
                                    Button(
                                        type = "web_url",
                                        title = messages.getMessage("chatbot.view-details", arrayOf(), locale),
                                        url = urlBuilder.toPropertyUrl(room, request),
                                    )
                                )
                            )
                        }
                    ),
                )
            ),
        )
        messenger.send(messaging.recipient.id, request)
    }

    private fun toSubTitle(property: RoomSummary, tenant: Tenant, locale: Locale): String {
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

    private fun sendTextKey(key: String, messaging: Messaging, locale: Locale, params: Array<Any> = emptyArray()) {
        val text = messages.getMessage(key, params, locale)
        sendText(text, messaging)
    }

    private fun sendText(text: String, messaging: Messaging) {
        messenger.send(
            pageId = messaging.recipient.id,
            request = SendRequest(
                recipient = Party(messaging.sender.id),
                message = Message(
                    text = text,
                ),
            ),
        )
    }
}
