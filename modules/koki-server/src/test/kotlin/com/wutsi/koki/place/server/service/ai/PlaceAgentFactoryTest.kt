package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class PlaceAgentFactoryTest {
    private val chat = mock<LLM>()
    private val chatWithTools = mock<LLM>()
    private val vision = mock<LLM>()
    private val llmProvider = LLMProvider(chat, chatWithTools, vision)

    @Test
    fun createNeighborhoodContentGeneratorAgent() {
        val factory = PlaceAgentFactory(llmProvider)

        val neighbourhood = LocationEntity(
            id = 1L,
            name = "Downtown",
            type = LocationType.NEIGHBORHOOD
        )
        val city = LocationEntity(
            id = 2L,
            name = "Montreal",
            type = LocationType.CITY
        )

        // WHEN
        val agent = factory.createNeighborhoodContentGeneratorAgent(neighbourhood, city)

        // THEN
        assertNotNull(agent)
        assertTrue(agent is NeighbourhoodContentGeneratorAgent)
    }
}
