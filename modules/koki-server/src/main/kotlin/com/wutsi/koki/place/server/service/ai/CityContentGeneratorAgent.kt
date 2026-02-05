package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.Tool
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.http.MediaType

class CityContentGeneratorAgent(
    val city: LocationEntity,
    val llm: LLM,
    maxIterations: Int = 30,
) : Agent(llm, maxIterations = maxIterations, responseType = MediaType.APPLICATION_JSON) {
    override fun buildPrompt(query: String, memory: List<String>): String {
        val prompt = this::class.java.getResourceAsStream("/place/prompt/city-content-generator.prompt.md")!!
            .reader()
            .readText()

        return prompt.replace("{{country}}", city.country)
            .replace(
                "{{tools}}",
                tools().joinToString("\n") { tool ->
                    "- ${tool.function().name} - ${tool.function().description}"
                }
            )
            .replace("{{city}}", city.name)
            .replace("{{observations}}", memory.joinToString("\n") { entry -> "- $entry" })
    }

    override fun tools(): List<Tool> = llm.getBuiltInTools()
}

data class CityContentGeneratorResult(
    val summary: String? = null,
    val introduction: String? = null,
    val description: String? = null,
    val summaryFr: String? = null,
    val introductionFr: String? = null,
    val descriptionFr: String? = null,
)
