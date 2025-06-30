package com.wutsi.koki.chatbot.ai.agent

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchParameterAgentTest {
    private val objectMapper = ObjectMapper()
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )

    @BeforeEach
    fun setUp() {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    @Test
    fun `search in city`() {
        val json = SearchParameterAgent(llm).run("I look for apartment in Yaounde")

        val result = objectMapper.readValue(json, SearchParameters::class.java)
        assertEquals("APARTMENT", result.propertyType)
        assertEquals("Yaounde", result.city)
        assertEquals(null, result.minBedrooms)
        assertEquals(null, result.maxBedrooms)
        assertEquals(null, result.minBudget)
        assertEquals(null, result.maxBudget)
        assertEquals("UNKNOWN", result.leaseType)
        assertEquals("UNKNOWN", result.furnishedType)
        assertEquals(true, result.valid)
        assertEquals(null, result.invalidReason)
    }

    @Test
    fun `search in neighborhood`() {
        val json = SearchParameterAgent(llm).run("I look for a 3 bedrooms in Bastos, Yaounde")

        val result = objectMapper.readValue(json, SearchParameters::class.java)
        assertEquals("Yaounde", result.city)
        assertEquals("Bastos", result.neighborhood)
        assertEquals("UNKNOWN", result.propertyType)
        assertEquals(3, result.minBedrooms)
        assertEquals(null, result.maxBedrooms)
        assertEquals(null, result.minBudget)
        assertEquals(null, result.maxBudget)
        assertEquals("UNKNOWN", result.leaseType)
        assertEquals("UNKNOWN", result.furnishedType)
        assertEquals(true, result.valid)
        assertEquals(null, result.invalidReason)
    }

    @Test
    fun `search in with budget`() {
        val json = SearchParameterAgent(llm)
            .run("I'm looking for a furnished room in Yaounde for 3 days, for 75000F/day")

        val result = objectMapper.readValue(json, SearchParameters::class.java)
        assertEquals("Yaounde", result.city)
        assertEquals(null, result.neighborhood)
        assertEquals("ROOM", result.propertyType)
        assertEquals(null, result.minBedrooms)
        assertEquals(null, result.maxBedrooms)
        assertEquals(75000.0, result.minBudget)
        assertEquals(75000.0, result.maxBudget)
        assertEquals("SHORT_TERM", result.leaseType)
        assertEquals("FULLY_FURNISHED", result.furnishedType)
        assertEquals(true, result.valid)
        assertEquals(null, result.invalidReason)
    }

    @Test
    fun invalid() {
        val json = SearchParameterAgent(llm).run("Hello. I look for nice girls for tonight")

        val result = objectMapper.readValue(json, SearchParameters::class.java)
        assertEquals(false, result.valid)
        assertNotNull(result.invalidReason)
    }
}
