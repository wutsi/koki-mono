package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType

/**
 * This agent extract information from room images
 */
class RoomImageAgent(
    val llm: LLM,
    val maxIterations: Int = 5,
) : Agent(llm, maxIterations, MediaType.APPLICATION_JSON) {
    override fun systemInstructions() =
        """
            You are a real estate agent helping customers to rent or by properties.
            You analyze pictures provided to extract informations to describe the property.
            Provide accurate and detailed information about the picture, in a format that is SEO friendly and suitable for websites like AirBnB, VRBO or Bookings.com.

            Return the information in JSON with the following information:
            - title: Title of the image. If this image represent a room of a property, Include the name of room in the title
            - description: Short description of the image that has a maximum of 255 characters.
            - hashtags: List of hashtags (up to 5)
            - quality: Quality of the image, with the values 0=LOW, 1=MEDIUM or 2=HIGH
            - valid: (true|false) "true" when the image is valid for an online property listing
            - reason: If the image is not valid, explain why.

            If you cannot extract the information fro the provide image, you should return valid as "false", and the title, description and hashtag as null
    """.trimIndent()

    override fun buildPrompt(query: String, memory: List<String>): String {
        return """
            Goal: Extract informations from the provided image.
            Query: {{query}}
        """.trimIndent()
            .replace("{{query}}", query)
            .replace("{{observation}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}

data class RoomImageAgentData(
    val title: String? = null,
    val description: String? = null,
    val hashtags: List<String>? = null,
    val quality: Int = 0,
    val valid: Boolean = false,
    val reason: String? = null,
)
