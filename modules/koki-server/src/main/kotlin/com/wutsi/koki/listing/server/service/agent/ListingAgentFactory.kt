package com.wutsi.koki.listing.server.service.agent

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service

@Service
class ListingAgentFactory(private val llmProvider: LLMProvider) {
    fun createImageReviewerAgent(): ListingImageReviewerAgent {
        return ListingImageReviewerAgent(llm = llmProvider.visionLLM)
    }

    fun createDescriptorAgent(
        listing: ListingEntity,
        images: List<FileEntity>,
        city: LocationEntity?,
        neighbourhood: LocationEntity?,
    ): ListingDescriptorAgent {
        return ListingDescriptorAgent(
            llm = llmProvider.visionLLM,
            listing = listing,
            images = images,
            city = city,
            neighbourhood = neighbourhood,
        )
    }
}
