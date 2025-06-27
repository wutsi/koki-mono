package com.wutsi.koki.chatbot.telegram.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.chatbot.telegram.form.TrackForm
import com.wutsi.koki.chatbot.telegram.refdata.service.LocationService
import com.wutsi.koki.chatbot.telegram.room.model.RoomLocationMetricModel
import com.wutsi.koki.chatbot.telegram.room.service.RoomLocationMetricService
import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.chatbot.telegram.tracking.service.TrackService
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.track.dto.TrackEvent
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Locale

@Service
class SearchHandler(
    private val client: TelegramClient,
    private val agentFactory: AgentFactory,
    private val tenantProvider: TenantProvider,
    private val tenantService: TenantService,
    private val objectMapper: ObjectMapper,
    private val metricService: RoomLocationMetricService,
    private val locationService: LocationService,
    private val telegramUrlBuilder: TelegramUrlBuilder,
    private val trackService: TrackService,
    private val messages: MessageSource,
) : CommandHandler {
    companion object {
        const val SEARCHING = "handler.search.searching"
        const val LOCATION_FOR_RENTAL = "handler.search.for-rental"
        const val PRICE_PER_MONTH = "handler.search.price-per-month"
        const val PRICE_PER_NIGHT = "handler.search.price-per-night"
        const val BEDROOM = "handler.search.n-bedroom"
        const val BATHROOM = "handler.search.n-bathroom"
        const val NOT_FOUND = "handler.search.not-found"
        const val FIND_MORE = "handler.search.find-more"
    }

    override fun handle(update: Update) {
        val tenant = tenantService.tenant(tenantProvider.id() ?: -1)
        val query = update.message.text.substring(7).trim()
        search(query, update, tenant)
    }

    private fun search(query: String, update: Update, tenant: TenantModel) {
        // Searching....
        val locale = Locale(update.message.from.languageCode)
        client.execute(
            SendMessage(
                update.message.chatId.toString(),
                messages.getMessage(SEARCHING, emptyArray(), locale),
            )
        )

        // Search
        val result = findProperties(query)
        if (result.properties.isEmpty()) {
            notFound(result.searchParameters, update, locale)
        } else {
            result.properties.map { property -> sendProperty(property, tenant, update, locale) }
            if (result.properties.size >= SearchRoomTool.MAX_RECOMMENDATIONS) {
                sendViewMoreCTA(result, tenant, update, locale)
            }
            track(result.properties, update)
        }
    }

    private fun notFound(search: SearchParameters, update: Update, locale: Locale) {
        val tenantId = tenantProvider.id() ?: -1
        val tenant = tenantService.tenant(tenantId)

        var metrics = emptyList<RoomLocationMetricModel>()
        if (search.cityId != null) { // Search in city...
            metrics = metricService.metrics(
                // Find neighborhood where there are properties
                country = tenant.country,
                locationType = LocationType.NEIGHBORHOOD,
                limit = 10,
            )
        }
        if (metrics.isEmpty()) { // Fallback to cities where there are properties
            metrics = metricService.metrics(
                country = tenant.country,
                locationType = LocationType.CITY,
                limit = 10,
            )
        }

        val msg = SendMessage(update.message.chatId.toString(), NOT_FOUND)
        if (metrics.isNotEmpty()) {
            msg.replyMarkup = InlineKeyboardMarkup(
                metrics.map { metric ->
                    val button = InlineKeyboardButton(
                        messages.getMessage(LOCATION_FOR_RENTAL, arrayOf(metric.location.name), locale)
                    )
                    button.url = telegramUrlBuilder.toLocationUrl(metric.location, tenant, update)
                    InlineKeyboardRow(button)
                }
            )
        }
        client.execute(msg)
    }

    private fun findProperties(query: String): SearchAgentData {
        val agent = agentFactory.crateSearchAgent()
        val json = agent.run(query)
        return objectMapper.readValue(json, SearchAgentData::class.java)
    }

    private fun sendProperty(property: PropertyData, tenant: TenantModel, update: Update, locale: Locale) {
        // Price
        val fmt = tenant.createMoneyFormat()
        val price = if (property.pricePerMonth != null) {
            messages.getMessage(PRICE_PER_MONTH, arrayOf(fmt.format(property.pricePerMonth)), locale)
        } else {
            messages.getMessage(PRICE_PER_NIGHT, arrayOf(fmt.format(property.pricePerNight)), locale)
        }

        // Bedroom
        val bedroom = messages.getMessage(BEDROOM, arrayOf(property.bedrooms), locale)

        // bathroom
        val bathroom = if (property.bathrooms > 0) {
            messages.getMessage(BATHROOM, arrayOf(property.bathrooms), locale)
        } else {
            null
        }
        val title = listOf(price, bedroom, bathroom).filterNotNull().joinToString(" | ")

        // Location
        val location = listOf(property.neighborhood, property.city).filterNotNull().joinToString(", ")

        // URL
        val url = telegramUrlBuilder.toPropertyUrl(property, tenant, update)
        val text = "**$title**\n$location\n\n$url"
        val msg = SendMessage(update.message.chatId.toString(), text)
        msg.enableMarkdown(true)
        client.execute(msg)
    }

    private fun sendViewMoreCTA(data: SearchAgentData, tenant: TenantModel, update: Update, locale: Locale) {
        val cta = InlineKeyboardButton(messages.getMessage(FIND_MORE, emptyArray(), locale))
        cta.url = telegramUrlBuilder.toViewMoreUrl(data, tenant, update)

        val locationId = (data.searchParameters.neighborhoodId ?: data.searchParameters.cityId) ?: return
        val location = locationService.location(locationId)

        val msg = SendMessage(
            update.message.chatId.toString(),
            messages.getMessage(LOCATION_FOR_RENTAL, arrayOf(location.name), locale)
        )
        msg.replyMarkup = InlineKeyboardMarkup(
            listOf(
                InlineKeyboardRow(cta)
            )
        )
        client.execute(msg)
    }

    private fun track(properties: List<PropertyData>, update: Update) {
        trackService.track(
            TrackForm(
                time = System.currentTimeMillis(),
                deviceId = update.message.from.id.toString(),
                event = TrackEvent.IMPRESSION,
                productId = properties.map { property -> property.id.toString() }.joinToString("|"),
            )
        )
    }
}
