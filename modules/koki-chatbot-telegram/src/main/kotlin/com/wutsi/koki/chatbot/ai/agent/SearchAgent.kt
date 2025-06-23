package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType

class SearchAgent(
    val llm: LLM,
    val searchRoomTool: SearchRoomTool,
) : Agent(llm, 10, MediaType.APPLICATION_JSON) {
    companion object {
        val SYSTEM_INSTRUCTIONS = """
            You are an assistant who helps users to find properties for rental.

            Return the properties found in an JSON array with the following information for each entry:
            {
              "properties":[
                {
                    "url": "URL of the property listing",
                    "pricePerMonth": 1289,
                    "pricePerNight": 75,
                    "currency": "Currency code. Ex: CAD",
                    "city": "Name of the city. Ex: Montreal",
                    "neighborhood": "Name of the neighborhood. Ex: Centre Ville",
                    "bedrooms": 3,
                    "bathrooms": 2,
                    "area": 4334,
                    "title": "Title of the property",
                    "summary": "Short summary description of the property"
                }
              ]
            }
            If you cannot find any property, return an empty array.
        """.trimIndent()

        val PROMPT = """
            Goal: Search properties
            Query: {{query}}

            Observations:
            {{observations}}
        """.trimIndent()
    }

    override fun systemInstructions(): String {
        return SYSTEM_INSTRUCTIONS
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        return PROMPT
            .replace("{{query}}", query)
            .replace("{{observations}}", memory.map { entry -> "- $entry" }.joinToString("\n"))
    }

    override fun tools(): List<Tool> {
        return listOf(
            searchRoomTool,
        )
    }
}
