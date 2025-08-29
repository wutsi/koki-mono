package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class ListingAgentFactory(
    private val llmProvider: LLMProvider,
    private val locationService: LocationService,
) {
    fun createImageReviewerAgent(tenantId: Long): ListingImageReviewerAgent {
        val llm = llmProvider.get(tenantId)
        return ListingImageReviewerAgent(llm = llm)
    }

    fun createDescriptorAgent(listing: ListingEntity, tenantId: Long): ListingDescriptorAgent {
        val llm = llmProvider.get(tenantId)
        return ListingDescriptorAgent(
            llm = llm,
            listing = listing,
            locationService = locationService,
        )
    }
}
