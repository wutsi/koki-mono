package com.wutsi.koki.listing.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class ListingAgentFactory(
    private val amenityService: AmenityService,
    private val locationService: LocationService,
    private val llmProvider: LLMProvider
) {
    fun createImageContentGenerator(): Agent {
        return ListingImageContentGeneratorAgent(llm = llmProvider.visionLLM)
    }

    fun createListingContentGenerator(
        listing: ListingEntity,
        images: List<FileEntity>,
        city: LocationEntity?,
        neighbourhood: LocationEntity?,
    ): Agent {
        return ListingContentGeneratorAgent(
            llm = llmProvider.chatLLM,
            listing = listing,
            images = images,
            city = city,
            neighbourhood = neighbourhood,
        )
    }

    fun createListingContentParserAgent(city: LocationEntity): Agent {
        return ListingContentParserAgent(
            amenityService = amenityService,
            locationService = locationService,
            city = city,
            llm = llmProvider.chatLLM,
        )
    }

    fun createListingLocationExtractoryAgent(country: String): Agent {
        return ListingLocationExtractoryAgent(
            country = country,
            llm = llmProvider.chatLLM,
        )
    }
}
