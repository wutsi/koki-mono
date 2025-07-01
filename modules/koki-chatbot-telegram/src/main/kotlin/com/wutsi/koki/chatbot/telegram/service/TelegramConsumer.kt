package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.chatbot.Chatbot
import com.wutsi.koki.chatbot.ChatbotRequest
import com.wutsi.koki.chatbot.ChatbotResponse
import com.wutsi.koki.chatbot.InvalidQueryException
import com.wutsi.koki.chatbot.UrlBuilder
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileSummary
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.platform.logger.DefaultKVLogger
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
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.net.URI
import java.text.DecimalFormat
import java.util.Locale
import java.util.UUID
import kotlin.collections.forEach

@Service
class TelegramConsumer(
    private val telegram: TelegramClient,
    private val chatbot: Chatbot,
    private val tenantProvider: TenantProvider,
    private val kokiTenants: KokiTenants,
    private val kokiFiles: KokiFiles,
    private val messages: MessageSource,
    private val publisher: Publisher,
    private val languageDetector: LanguageDetector,
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
        val tenant = kokiTenants.tenant(tenantId ?: -1).tenant
        logger.add("tenant_id", tenantId)

        val language = detectLanguage(update)
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
            logger.add("room_hero_imageIds", response.rooms.map { room -> room.heroImageId })

            if (response.rooms.isNotEmpty()) {
                val urlBuilder = UrlBuilder(baseUrl = tenant.clientPortalUrl, medium = "telegram")

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
                sendProperties(response.rooms, images, update, request, response, tenant, locale, urlBuilder)

                // Track impression
                trackImpression(response.rooms, update)
            } else {
                sendTextKey("chatbot.not-found", update, locale)
            }
        } catch (ex: InvalidQueryException) {
            logger.add("failure_reason", ex.message)
            sendTextKey("chatbot.help", update, locale, arrayOf(Locale(language, tenant.country).displayCountry))
        } catch (ex: Exception) {
            logger.setException(ex)
            sendTextKey("chatbot.error", update, locale)
        }
    }

    private fun sendProperties(
        rooms: List<RoomSummary>,
        images: Map<Long, FileSummary>,
        update: Update,
        request: ChatbotRequest,
        response: ChatbotResponse,
        tenant: Tenant,
        locale: Locale,
        urlBuilder: UrlBuilder
    ) {
        val similarUrl = if (response.searchLocation != null && response.searchParameters != null) {
            urlBuilder.toViewMoreUrl(response.searchParameters!!, request, response.searchLocation!!)
        } else {
            null
        }

        rooms.forEach { room ->
            val title = toTitle(room, tenant, locale)
            val detailsUrl = urlBuilder.toPropertyUrl(room, request)
            val image = room.heroImageId?.let { id -> images[id] }
            if (image != null) {
                sendPhoto(
                    image = image,
                    caption = title,
                    detailsTitle = messages.getMessage("chatbot.view-details", arrayOf(), locale),
                    detailsUrl = detailsUrl,
                    similarTitle = messages.getMessage("chatbot.similar-properties", arrayOf(), locale),
                    similarUrl = similarUrl,
                    update = update
                )
            } else {
                sendText(
                    text = "$title\n\n$detailsUrl",
                    update = update
                )
            }
        }

        if (similarUrl != null) {
            sendLink(
                text = messages.getMessage("chatbot.find-more-text", arrayOf(), locale),
                url = similarUrl,
                urlTitle = messages.getMessage("chatbot.find-more", arrayOf(), locale),
                update = update
            )
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

    private fun sendPhoto(
        image: FileSummary,
        caption: String,
        detailsTitle: String,
        detailsUrl: String,
        similarTitle: String,
        similarUrl: String?,
        update: Update
    ) {
        val photo = InputFile()
        val stream = URI.create(image.url).toURL().openStream()
        stream.use {
            photo.setMedia(stream, image.name)
            val msg = SendPhoto.builder()
                .chatId(update.message.chatId.toString())
                .photo(photo)
                .caption(caption)
                .replyMarkup(
                    InlineKeyboardMarkup.builder()
                        .keyboard(
                            listOf(
                                InlineKeyboardRow(
                                    InlineKeyboardButton.builder()
                                        .url(detailsUrl)
                                        .text(detailsTitle)
                                        .build(),
                                ),
                                similarUrl?.let { url ->
                                    InlineKeyboardRow(
                                        InlineKeyboardButton.builder()
                                            .url(url)
                                            .text(similarTitle)
                                            .build()
                                    )
                                }
                            ).filterNotNull()
                        ).build()
                ).build()
            telegram.execute(msg)
        }
    }

    private fun sendLink(
        text: String,
        url: String,
        urlTitle: String,
        update: Update
    ) {
        val msg = SendMessage.builder()
            .chatId(update.message.chatId.toString())
            .text(text)
            .replyMarkup(
                InlineKeyboardMarkup.builder()
                    .keyboard(
                        listOf(
                            InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                    .url(url)
                                    .text(urlTitle)
                                    .build()
                            )
                        )
                    ).build()
            ).build()
        telegram.execute(msg)
    }

    private fun detectLanguage(update: Update): String {
        val lang = languageDetector.detect(update.message.text).language

        return when (lang) {
            "en" -> "en"
            else -> update.message.from.languageCode
        }
    }
}
