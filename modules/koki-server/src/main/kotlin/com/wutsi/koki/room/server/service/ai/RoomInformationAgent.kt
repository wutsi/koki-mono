package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.server.domain.RoomEntity
import org.springframework.http.MediaType
import java.util.Locale

/**
 * This agent the describe properties
 */
class RoomInformationAgent(
    val room: RoomEntity,
    val llm: LLM,
    val accountService: AccountService,
    val amenityService: AmenityService,
    val locationService: LocationService,
    val maxIterations: Int = 5,
) : Agent(llm, maxIterations, MediaType.APPLICATION_JSON) {
    override fun systemInstructions() =
        """
            You are a real estate agent helping customers to rent or buy properties.
            You analyze all the images provided to provide an accurate and detailed information of the property.

            You must return the property description in JSON that looks like:
            {
              "title": "Title of the property",
              "summary": "Short summary",
              "description": "A more comprehensive description",
              "titleFr": "Title translated in french",
              "summaryFr": "Short summary translated in french",
              "descriptionFr": "Description  translated in french",
              "heroImageIndex": "Index of the hero image (0 based index),
              "heroImageReason": "Reason why you have chosen the image",
              "amenityIds": [1001, 1002, 1003],
              "numberOfImages": "Number of image analyzed",
              "numberOfBedrooms": "Number of bedrooms in the property",
              "numberOfBeds": "Number of beds in the property",
              "numberOfBathrooms": "Number of bathrooms in the property",
              "valid": "(true|false) "true" when you are able to extract the property information from the images."
            }

            Instructions:
            - Instructions for crafting the title:
              - Highlight the Unique Selling Proposition: What makes your property stand out? Is it a stunning view, a unique amenity, a prime location, or a special experience? Lead with that!
              - Be Specific and Descriptive: Use keywords that potential guests are likely to search for. Include details about the type of property, location, and key features.
              - Include Location Benefits: Mentioning nearby attractions, neighborhoods, or ease of access can be a big draw.
              - Use Strong and Evocative Language: Words like "stunning," "charming," "luxury," "private," and "unique" can create a more appealing image.
              - Keep it Concise and Readable: Aim for a title that is easy to scan and understand quickly. Aim for a maximum of 50 characters
            - Instructions for crafting the description:
              - Start with Compelling Opening: Your first sentence or two should immediately grab attention and reinforce your unique selling proposition from your title.
              - For fully furnished properties, expand on key Features and amenities: Now's the time to elaborate on the highlights mentioned in your title and introduce other enticing features. Be specific and descriptive.
              - Highlight the experience: Think about what makes your property special. Is it the peace and quiet, the convenience to attractions, the luxurious amenities (for fully furnished properties), or the thoughtful touches you provide?
              - Aim for around 300-400 words.
            - Instructions for crafting the summary:
              - Expand on the Title and Hook
              - Focus on the Benefits for the Sharer's Audience: Why should someone click on this link? What kind of experience awaits the customer?
              - Use Action-Oriented Language: Encourage clicks and create a sense of desire.
              - Aim for around 150-160 characters.

    """.trimIndent()

    override fun buildPrompt(query: String, memory: List<String>): String {
        val locationIds = listOf(room.neighborhoodId, room.cityId).filterNotNull()
        val location = locationService.search(
            ids = locationIds,
            limit = locationIds.size
        ).map { loc -> loc.name }.joinToString(",")
        val country = room.country?.let { Locale("en", room.country).displayName }

        val hotel = if (room.type == RoomType.HOTEL_ROOM) {
            val account = accountService.get(room.accountId, room.tenantId)
            "- Hotel: ${account.name}"
        } else {
            ""
        }

        return """
            Goal: Create the detailed description of a property listing.
            Query: $query

            Property Information:
            - Location: {{location}}
            - Type: ${room.type}
            - Bedrooms: ${room.numberOfRooms}
            - Beds: ${room.numberOfBeds}
            - Baths: ${room.numberOfBathrooms}
            - Max guests: ${room.maxGuests}
            - Furnished: ${room.furnishedType}
            {{hotel}}

            Amenities:
            Here are all the amenities in CSV format:
            id,name
            {{amenities}}

            Observations:
            {{observations}}
        """.trimIndent()
            .replace("{{observations}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
            .replace(
                "{{amenities}}",
                amenityService.all()
                    .map { entry -> "${entry.id},${entry.name}" }
                    .joinToString("\n")
            )
            .replace(
                "{{location}}",
                country?.let { listOf(location, country).joinToString(",") } ?: "Unknown"
            )
            .replace("{{hotel}}", hotel)
    }

    override fun tools(): List<Tool> = emptyList()
}
