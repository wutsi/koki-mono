package com.wutsi.koki.room.server.server.agent

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.platform.ai.llm.LLMNotConfiguredException
import org.springframework.stereotype.Service

@Service
class ListingAgentFactory(
    private val llmProvider: LLMProvider,
) {
    fun createImageReviewerAgent(tenantId: Long): ImageReviewerAgent? {
        try {
            val llm = llmProvider.get(tenantId)
            return ImageReviewerAgent(llm = llm)
        } catch (ex: LLMNotConfiguredException) {
            return null
        }
    }
}
