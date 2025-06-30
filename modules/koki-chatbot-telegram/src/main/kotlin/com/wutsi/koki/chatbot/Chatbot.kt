package com.wutsi.koki.chatbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms

class Chatbot(
    private val kokiRefData: KokiRefData,
    private val kokiRooms: KokiRooms,
    private val agentFactory: AgentFactory,
    private val objectMapper: ObjectMapper,
    private val maxRecommendation: Int,
) {
    fun process(request: ChatbotRequest): ChatbotResponse {
        val searchParameters = run(request)
        if (!searchParameters.valid) {
            throw InvalidQueryException(message = searchParameters.invalidReason)
        } else {
            val city =
                searchParameters.city?.let { location -> getLocation(location, LocationType.CITY, request.country) }

            val neighborhood = searchParameters.neighborhood?.let { location ->
                getLocation(
                    location,
                    LocationType.NEIGHBORHOOD,
                    request.country,
                    city?.id
                )
            }

            val rooms = search(city, neighborhood, searchParameters)
            return ChatbotResponse(
                rooms = rooms,
                searchLocation = neighborhood ?: city,
                searchParameters = searchParameters
            )
        }
    }

    private fun run(request: ChatbotRequest): SearchParameters {
        try {
            val json = agentFactory.createSearchParameterAgent().run(request.query)
            return objectMapper.readValue(json, SearchParameters::class.java)
        } catch (ex: Exception) {
            throw ChatbotException("Unable to run the AI Agent", ex)
        }
    }

    private fun search(city: Location?, neighborhood: Location?, params: SearchParameters): List<RoomSummary> {
        return kokiRooms.rooms(
            cityId = city?.id,
            neighborhoodId = neighborhood?.id,
            minRooms = params.minBedrooms,
            maxRooms = params.maxBedrooms,
            minBudget = params.minBudget,
            maxBudget = params.maxBudget,
            ids = emptyList(),
            status = RoomStatus.PUBLISHED,
            amenityIds = emptyList(),
            categoryIds = emptyList(),
            accountIds = emptyList(),
            accountManagerIds = emptyList(),
            minBathrooms = null,
            maxBathrooms = null,
            totalGuests = null,

            types = params.propertyType?.let { type ->
                RoomType.valueOf(type.uppercase())
                    .let { value -> if (value == RoomType.UNKNOWN) null else value }
                    ?.let { value -> listOf(value) }
            } ?: emptyList(),

            leaseType = params.leaseType?.let { type ->
                LeaseType.valueOf(type.uppercase())
                    .let { value -> if (value == LeaseType.UNKNOWN) null else value }
            },

            furnishedType = params.furnishedType?.let { type ->
                FurnishedType.valueOf(type.uppercase())
                    .let { value -> if (value == FurnishedType.UNKNOWN) null else value }
            },

            limit = maxRecommendation,
            offset = 0,
        ).rooms
    }

    private fun getLocation(keyword: String, type: LocationType, country: String, parentId: Long? = null): Location? {
        return kokiRefData.locations(
            keyword = keyword,
            type = type,
            country = country,
            ids = emptyList(),
            parentId = parentId,
            limit = 1,
            offset = 0,
        ).locations.firstOrNull()
    }
}
