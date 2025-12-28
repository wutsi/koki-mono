package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import org.springframework.http.MediaType

/**
 * This API agent review images uploaded
 */
class ListingImageContentGeneratorAgent(
    val llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    companion object {
        const val QUERY = ""
    }

    override fun systemInstructions(): String? {
        return null
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/listing/prompt/listing-image-content-generator.prompt.md")!!
            .reader()
            .readText()
        return prompt.replace("{{observations}}", memory.joinToString("\n") { entry -> "- $entry" })
    }

    override fun tools(): List<Tool> = emptyList<Tool>()
}

data class ListingImageContentGeneratorResult(
    val title: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val descriptionFr: String? = null,
    val quality: ImageQuality? = null,
    val valid: Boolean = false,
    val reason: String? = null,
)
