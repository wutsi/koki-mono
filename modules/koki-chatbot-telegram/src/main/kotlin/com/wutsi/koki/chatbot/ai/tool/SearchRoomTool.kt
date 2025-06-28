package com.wutsi.koki.chatbot.ai.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.FunctionDeclaration
import com.wutsi.koki.platform.ai.llm.FunctionParameterProperty
import com.wutsi.koki.platform.ai.llm.FunctionParameters
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.springframework.stereotype.Service

@Service
class SearchRoomTool(
    private val kokiRooms: KokiRooms,
    private val kokiRefData: KokiRefData,
    private val objectMapper: ObjectMapper,
) : Tool {
    companion object {
        const val MAX_RECOMMENDATIONS = 3
        const val NAME = "search_room_tools"
        const val DESCRIPTION = "Search properties available for rental in a given city"
        const val MESSAGE_NOT_FOUND = "No property found for the specified location"
        const val MESSAGE_FOUND = "Here are the properties found, in JSON format"
    }

    override fun function(): FunctionDeclaration {
        return FunctionDeclaration(
            name = NAME,
            description = DESCRIPTION,
            parameters = FunctionParameters(
                type = Type.OBJECT,
                properties = mapOf(
                    "country" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Country where to search the properties for rental. It must be in the ISO 3166-1 alpha-2 format (Ex: US for United States, CA for Canada etc.)",
                    ),
                    "neighborhood" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Name of the neighborhood where to search the properties for rental",
                    ),
                    "city" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Name of the city where to search the properties for rental",
                    ),
                    "propertyType" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Type of property",
                        enum = RoomType.entries
                            .filter { entry -> entry != RoomType.UNKNOWN }
                            .map { entry -> entry.name }
                    ),
                    "minBedrooms" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "Minimal number of bedrooms",
                    ),
                    "maxBedrooms" to FunctionParameterProperty(
                        type = Type.INTEGER,
                        description = "Maximum number of bedrooms",
                    ),
                    "minBudget" to FunctionParameterProperty(
                        type = Type.NUMBER,
                        description = "Minimal rental price",
                    ),
                    "maxBudget" to FunctionParameterProperty(
                        type = Type.NUMBER,
                        description = "Maximum rental price",
                    ),
                    "leaseType" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Short term rental or Long term rental?",
                        enum = LeaseType.entries
                            .filter { entry -> entry != LeaseType.UNKNOWN }
                            .map { entry -> entry.name }
                    ),
                    "furnishedType" to FunctionParameterProperty(
                        type = Type.STRING,
                        description = "Furnished or not?",
                        enum = FurnishedType.entries
                            .filter { entry -> entry != FurnishedType.UNKNOWN }
                            .map { entry -> entry.name }
                    ),
                )
            )
        )
    }

    override fun use(args: Map<String, Any>): String {
        // Location
        val country = args["country"]?.toString()
        val city = args["city"]?.toString()?.let { location ->
            resolveLocation(
                country = country,
                location = location,
                type = LocationType.CITY,
            )
        }
        val neighborhood = args["neighborhood"]?.toString()?.let { location ->
            resolveLocation(
                country = country,
                location = location,
                type = LocationType.NEIGHBORHOOD,
            )
        }

        // Search
        val rooms = searchProperties(
            city = city,
            neighborhood = neighborhood,
            propertyType = args["propertyType"]?.toString(),
            minBedrooms = args["minBedrooms"]?.toString()?.toInt(),
            maxBedrooms = args["maxBedrooms"]?.toString()?.toInt(),
            minBudget = args["minBudget"]?.toString()?.toDouble(),
            maxBudget = args["maxBudget"]?.toString()?.toDouble(),
            leaseType = args["leaseType"]?.toString(),
            furnishedType = args["furnishedType"]?.toString(),
        )
        if (rooms.isEmpty()) {
            return notFound()
        } else {
            return """
                $MESSAGE_FOUND:
                ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rooms)}
            """.trimIndent()
        }
    }

    private fun notFound(): String {
        return MESSAGE_NOT_FOUND
    }

    private fun resolveLocation(
        country: String?,
        location: String,
        type: LocationType?
    ): Location? {
        return kokiRefData.locations(
            keyword = location,
            country = country,
            type = type,
            ids = emptyList(),
            parentId = null,
            limit = 1,
            offset = 0,
        ).locations
            .firstOrNull()
    }

    private fun searchProperties(
        neighborhood: Location?,
        city: Location?,
        propertyType: String?,
        minBedrooms: Int?,
        maxBedrooms: Int?,
        minBudget: Double?,
        maxBudget: Double?,
        leaseType: String?,
        furnishedType: String?,
    ): List<RoomSummary> {
        return kokiRooms.rooms(
            cityId = city?.id,
            neighborhoodId = neighborhood?.id,
            minRooms = minBedrooms,
            maxRooms = maxBedrooms,
            types = propertyType?.let { type -> listOf(RoomType.valueOf(type.uppercase())) } ?: emptyList(),
            ids = emptyList(),
            status = RoomStatus.PUBLISHED,
            amenityIds = emptyList(),
            categoryIds = emptyList(),
            accountIds = emptyList(),
            accountManagerIds = emptyList(),
            minBathrooms = null,
            maxBathrooms = null,
            totalGuests = null,
            limit = MAX_RECOMMENDATIONS,
            offset = 0,
        ).rooms
    }
}
