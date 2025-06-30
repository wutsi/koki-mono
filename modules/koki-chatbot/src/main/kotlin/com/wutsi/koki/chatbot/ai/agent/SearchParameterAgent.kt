package com.wutsi.koki.chatbot.ai.agent

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType
import org.springframework.http.MediaType

class SearchParameterAgent(
    val llm: LLM,
) : Agent(llm, 10, MediaType.APPLICATION_JSON) {
    companion object {
        private fun values(list: List<Any>): String {
            return list.map { value -> value.toString() }.joinToString(" | ")
        }

        val SYSTEM_INSTRUCTIONS = """
            You are an assistant who helps users to find properties for rental.
            You analyse the query provided and extract the search parameters that your return in the following JSON format:
              {
                 "city": "Name of city",
                 "neighborhood": "Name of the neighborhood",
                 "propertyType": "Property type. The possible values are: ${values(RoomType.entries)}"
                 "minBedrooms": 4, /* Number of bedrooms */
                 "maxBedrooms": null,  /* Number of bathrooms */
                 "minBudget": 50000, /* minimal budget for the rental */
                 "maxBudget": 100000, /* maximal budget for the rental */
                 "leaseType": "Type of lease. The possible values are: ${values(LeaseType.entries)}"
                 "furnishedType": "Type of furnishes. The possible values are: ${values(FurnishedType.entries)}"
                 "valid": true, /* If should be 'false' if the provide query is not about searching rental properties */
                 "invalidReason": "The detailed reason why the query is not valid"
              }
        """.trimIndent()

        val PROMPT = """
            Goal: Extract the search parameter from the query
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
        return emptyList()
    }
}
