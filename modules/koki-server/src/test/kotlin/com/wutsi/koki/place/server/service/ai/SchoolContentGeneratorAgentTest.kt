package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class SchoolContentGeneratorAgentTest {
    //    private val llm = Kimi(
//        apiKey = System.getenv("KIMI_API_KEY"),
//        model = "kimi-k2-turbo-preview",
//        readTimeoutMillis = 120000,
//    )
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
        readTimeoutMillis = 120000,
    )
    private val city = LocationEntity(name = "Yaoundé", country = "CM")
    private val neighbourhood = LocationEntity(name = "Bastos", country = "CM")
    private val agent = SchoolContentGeneratorAgent("MAARIF SCHOOL BASTOS", city, neighbourhood, llm)

    @Test
    fun tools() {
        assertEquals(llm.getBuiltInTools().size, agent.tools().size)
    }

    @Test
    fun run() {
        val json = agent.run("")
        JsonMapper().readValue(json, SchoolContentGeneratorAgentTest::class.java)
//        assertNotNull(result.summary)
//        assertNotNull(result.description)
//        assertNotNull(result.introduction)
//        assertNotNull(result.summaryFr)
//        assertNotNull(result.descriptionFr)
//        assertNotNull(result.introductionFr)
//        assertNotNull(result.ratings)
    }
}
