package com.wutsi.koki.place.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service

@Service
class PlaceAgentFactory(private val llmProvider: LLMProvider) {
    fun createNeighborhoodContentGeneratorAgent(neighbourhood: LocationEntity, city: LocationEntity?): Agent {
        return NeighbourhoodContentGeneratorAgent(
            neighbourhood = neighbourhood,
            city = city,
            llm = llmProvider.chatLLM
        )
    }
}
