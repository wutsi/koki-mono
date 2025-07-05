package com.wutsi.koki.chatbot.messenger.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.ChatbotResponse
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
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.Tenant
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.net.URLEncoder
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
    private val logger: KVLogger,
    private val languageDetector: LanguageDetector,
    private val trackingService: TrackingService,

    @Value("\${koki.webapp.base-url}") private val webappUrl: String
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
        val tenantId = tenantProvider.id() ?: -1
        val tenant = kokiTenant.tenant(tenantId).tenant
        logger.add("tenant_id", tenantId)

        val language = detectLanguage(messaging)
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
            logger.add("room_hero_imageIds", response.rooms.map { room -> room.heroImageId })

            if (response.rooms.isNotEmpty()) {
                val correlationId = UUID.randomUUID().toString()
                val urlBuilder = UrlBuilder(baseUrl = tenant.clientPortalUrl, source = "messenger")

                // Images
                val images = kokiFiles.files(
                    ids = response.rooms.mapNotNull { room -> room.heroImageId },
                    limit = response.rooms.size,
                    offset = 0,
                    type = FileType.IMAGE,
                    status = FileStatus.APPROVED,
                    ownerId = null,
                    ownerType = null,
                ).files.associateBy { file -> file.id }

                // Rooms
                sendProperties(
                    rooms = response.rooms,
                    images = images,
                    request = request,
                    response = response,
                    tenant = tenant,
                    locale = locale,
                    urlBuilder = urlBuilder,
                    messaging = messaging,
                    correlationId = correlationId
                )

                // Track impression
                trackingService.impression(response.rooms, tenantId, correlationId, messaging)
            } else {
                sendTextKey("chatbot.not-found", messaging, locale)
            }
        } catch (ex: InvalidQueryException) {
            logger.add("failure_reason", ex.message)
            sendTextKey("chatbot.help", messaging, locale, arrayOf(Locale(language, tenant.country).displayCountry))
        } catch (ex: Exception) {
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
        response: ChatbotResponse,
        tenant: Tenant,
        locale: Locale,
        urlBuilder: UrlBuilder,
        messaging: Messaging,
        correlationId: String
    ) {
        val titleViewDetails = messages.getMessage("chatbot.view-details", arrayOf(), locale)
        val titleSimilarProperties = messages.getMessage("chatbot.similar-properties", arrayOf(), locale)
        val similarUrl = if (response.searchLocation != null && response.searchLocation != null) {
            urlBuilder.toViewMoreUrl(response.searchParameters!!, request, response.searchLocation!!)
        } else {
            null
        }

        // Properties
        var rank = 0
        messenger.send(
            messaging.recipient.id,
            SendRequest(
                recipient = Party(messaging.sender.id),
                message = Message(
                    attachment = Attachment(
                        type = "template",
                        payload = Payload(
                            template_type = "generic",
                            elements = rooms.map { room ->
                                val detailsUrl = "$webappUrl/click" +
                                    "?device-id=" + trackingService.deviceId(messaging) +
                                    "&product-id=${room.id}" +
                                    "&tenant-id=${tenant.id}" +
                                    "&rank=" + (rank++) +
                                    "&correlation-id=$correlationId" +
                                    "&url=" + URLEncoder.encode(urlBuilder.toPropertyUrl(room, request), "utf-8")

                                Element(
                                    title = if (locale.language == "en") room.title else room.titleFr,
                                    subtitle = toSubTitle(room, tenant, locale),
                                    image_url = room.heroImageId?.let { id -> images[id]?.url },
                                    default_action = Button(
                                        type = "web_url",
                                        url = urlBuilder.toPropertyUrl(room, request),
                                    ),
                                    buttons = listOf(
                                        Button(
                                            type = "web_url",
                                            title = titleViewDetails,
                                            url = detailsUrl,
                                        ),
                                        similarUrl?.let { url ->
                                            Button(
                                                type = "web_url",
                                                title = titleSimilarProperties,
                                                url = url,
                                            )
                                        },
                                    ).filterNotNull()
                                )
                            }
                        ),
                    )
                ),
            )
        )

        // View more
        if (similarUrl != null) {
            messenger.send(
                messaging.recipient.id,
                SendRequest(
                    recipient = Party(messaging.sender.id),
                    message = Message(
                        attachment = Attachment(
                            type = "template",
                            payload = Payload(
                                template_type = "button",
                                text = messages.getMessage("chatbot.find-more-text", arrayOf(), locale),
                                buttons = listOf(
                                    Button(
                                        type = "web_url",
                                        title = messages.getMessage("chatbot.find-more", arrayOf(), locale),
                                        url = similarUrl,
                                    )
                                ),
                            ),
                        )
                    ),
                )
            )
        }
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

    private fun detectLanguage(messaging: Messaging): String {
        val lang = messaging.message
            ?.text
            ?.let { text -> languageDetector.detect(text).language }

        return when (lang) {
            "en" -> "en"
            else -> "fr"
        }
    }
}
