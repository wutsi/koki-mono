package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import org.springframework.stereotype.Service

@Service
class ListingAgentFactory(private val llmProvider: LLMProvider) {
    fun createImageReviewerAgent(): Agent {
        return ListingImageReviewerAgent(llm = llmProvider.visionLLM)
    }

    fun createDescriptorAgent(
        listing: ListingEntity,
        images: List<FileEntity>,
        city: LocationEntity?,
        neighbourhood: LocationEntity?,
    ): Agent {
        return ListingDescriptorAgent(
            llm = llmProvider.visionLLM,
            listing = listing,
            images = images,
            city = city,
            neighbourhood = neighbourhood,
        )
    }

    fun createParserAgent(
        city: LocationEntity,
        amenityService: AmenityService
    ): Agent {
        return ListingParserAgent(
            amenityService = amenityService,
            city = city,
            llm = llmProvider.visionLLM,
        )
    }
}
