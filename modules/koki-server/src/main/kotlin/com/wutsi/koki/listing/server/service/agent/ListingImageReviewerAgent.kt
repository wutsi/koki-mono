package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType

/**
 * This agent review images
 */
class ListingImageReviewerAgent(
    val llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    companion object {
        val SYSTEM_INSTRUCTIONS = """
            You are a real estate agent helping customers to rent or buy properties.
            You analyze pictures provided to extract informations to describe the property.
            Provide accurate and detailed information about the picture, in a format that is SEO friendly and suitable for websites like AirBnB, VRBO or Bookings.com.

            Return the information in JSON with the following information:
            - title: Title of the picture
            - titleFr: Title translated in french
            - description: Description of the picture in less than 200 characters
            - descriptionFr: Description translated in french
            - quality: quality of the image, with the values POOR, LOW, MEDIUM or HIGH
            - valid: (true|false) "true" when the image is valid for an online property listing
            - reason: If the image is not valid, explain why.

            If you cannot extract the information from the provide image, you should return valid as "false", and all the other fields as "null"
        """.trimIndent()

        val PROMPT = """
            Goal: Extract informations from the provided image.
            Query: {{query}}

            Observations:
            {{observations}}
        """.trimIndent()

        const val QUERY = "Extract the information from the image provided"
    }

    override fun systemInstructions(): String {
        return SYSTEM_INSTRUCTIONS
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        return PROMPT
            .replace("{{query}}", query)
            .replace("{{observations}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}
