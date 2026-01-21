package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType

class ListingLocationExtractoryAgent(
    private val country: String,
    llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    override fun systemInstructions(): String? = null

    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/listing/prompt/listing-location-extractor.prompt.md")!!
            .reader()
            .readText()
            .replace("{{query}}", query)
            .replace("{{country}}", country)

        return prompt + memory.joinToString(separator = "\n", prefix = "\n", postfix = "\n")
    }
}

data class ListingLocationExtractoryResult(
    val street: String? = null,
    val neighbourhood: String? = null,
    val city: String? = null,
    val country: String? = null,
)
