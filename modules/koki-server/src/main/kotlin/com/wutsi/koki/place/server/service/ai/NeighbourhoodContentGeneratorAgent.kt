package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.agent.Tool
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.http.MediaType

class NeighbourhoodContentGeneratorAgent(
    val city: LocationEntity?,
    val neighbourhood: LocationEntity,
    val llm: LLM,
) : Agent(llm, responseType = MediaType.APPLICATION_JSON) {
    override fun systemInstructions(): String? {
        return null
    }

    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/place/prompt/neighbourhood-content-generator.prompt.md")!!
            .reader()
            .readText()

        return prompt.replace("{{country}}", neighbourhood.country)
            .replace("{{city}}", city?.name ?: "Unknown")
            .replace("{{neighbourhood}}", neighbourhood.name)
            .replace("{{observations}}", memory.joinToString("\n") { entry -> "- $entry" })
    }

    override fun tools(): List<Tool> = emptyList()
}

data class NeighbourhoodContentGeneratorResult(
    val summary: String? = null,
    val introduction: String? = null,
    val description: String? = null,
    val summaryFr: String? = null,
    val introductionFr: String? = null,
    val descriptionFr: String? = null,
    val ratings: NeighborhoodRatingResult = NeighborhoodRatingResult(),
)

data class NeighborhoodRatingResult(
    val security: RatingCriteraResult = RatingCriteraResult(),
    val amenities: RatingCriteraResult = RatingCriteraResult(),
    val infrastructure: RatingCriteraResult = RatingCriteraResult(),
    val lifestyle: RatingCriteraResult = RatingCriteraResult(),
    val commute: RatingCriteraResult = RatingCriteraResult(),
    val education: RatingCriteraResult = RatingCriteraResult(),
)

data class RatingCriteraResult(
    val value: Int = -1,
    val reason: String? = null,
)
