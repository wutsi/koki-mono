package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.service.AmenityService
import org.springframework.http.MediaType

/**
 * This agent the describe properties
 */
class RoomPublisherAgent(
    val llm: LLM,
    val amenityService: AmenityService,
    val maxIterations: Int = 5,
) : Agent(llm, maxIterations, MediaType.APPLICATION_JSON) {
    override fun systemInstructions() =
        """
            You are a real estate agent helping customers to rent or buy properties.
            You analyze pictures provided to extract informations to describe the property.
            Provide accurate and detailed information from thes picture, in a format that is SEO friendly and suitable for websites like AirBnB, VRBO or Bookings.com.

            Return the property description in JSON with the following information:
            - title: Title of property
            - description: Short description of the image that has a maximum of 1000 characters.
            - heroImageIndex: Index of the hero image that best represent the property (0 based index)
            - heroImageReason: Reason why you have chosen the image
            - amenityIds: Array containing the ID of the amenities identified in the image. Return only the amenities supported by the platform
            - valid: (true|false) "true" when you are able to extract the property description from the image.

            If you cannot extract the information from the provide images, you should return valid as "false" and all the other fields as "null"
    """.trimIndent()

    override fun buildPrompt(query: String, memory: List<String>): String {
        val amenities = amenityService.all()

        return """
            Goal: Create the detailed description of a property listing.
            Query: {{query}}

            Here is the list of supported amenities by the platform, in CSV format:
            id,name
            {{amenities}}

            Observations:
            {{observations}}
        """.trimIndent()
            .replace("{{query}}", query)
            .replace(
                "{{observations}}",
                memory.map { entry -> "- $entry" }.joinToString("\n")
            )
            .replace(
                "{{amenities}}",
                amenities.map { amenity -> "${amenity.id},${amenity.name}" }.joinToString("\n")
            )
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}
