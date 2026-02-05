package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class CityContentGeneratorAgentTest {
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
        readTimeoutMillis = 120000,
        websearchDelayMillis = 500,
    )
    private val city = LocationEntity(name = "Yaoundé", country = "CM")
    private val agent = CityContentGeneratorAgent(city, llm)

    @Test
    fun tools() {
        assertEquals(llm.getBuiltInTools().size, agent.tools().size)
    }

    @Test
    fun run() {
        val json = agent.run("")
        println(json)

        val result = JsonMapper().readValue(json, CityContentGeneratorResult::class.java)
        assertNotNull(result.summary)
        assertNotNull(result.description)
        assertNotNull(result.introduction)
        assertNotNull(result.summaryFr)
        assertNotNull(result.descriptionFr)
        assertNotNull(result.introductionFr)
    }
}
