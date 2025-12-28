package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingContentGeneratorAgent
import com.wutsi.koki.listing.server.service.ai.ListingContentGeneratorResult
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.service.LocationService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import java.util.Date

@Service
class ListingPublisher(
    private val listingService: ListingService,
    private val agentFactory: ListingAgentFactory,
    private val fileService: FileService,
    private val jsonMapper: JsonMapper,
    private val logger: KVLogger,
    private val locationService: LocationService
) {
    @Transactional
    fun publish(listingId: Long, tenantId: Long): ListingEntity? {
        val listing = listingService.get(listingId, tenantId)
        if (listing.status != ListingStatus.PUBLISHING) {
            logger.add("success", false)
            logger.add("error", "Invalid status")
            logger.add("listing_status", listing.status)
            return null
        }

        val images = fileService.search(
            tenantId = tenantId,
            ownerId = listing.id,
            ownerType = ObjectType.LISTING,
            status = FileStatus.APPROVED,
            type = FileType.IMAGE,
            limit = 100,
        )
        val city = listing.cityId?.let { id -> locationService.get(id) }
        val neighbourhood = listing.neighbourhoodId?.let { id -> locationService.get(id) }
        val agent = agentFactory.createListingContentGenerator(listing, images, city, neighbourhood)
        val json = agent.run(ListingContentGeneratorAgent.QUERY)
        val result = jsonMapper.readValue(json, ListingContentGeneratorResult::class.java)
        listing.status = ListingStatus.ACTIVE
        listing.title = result.title
        listing.summary = result.summary
        listing.description = result.description
        listing.titleFr = result.titleFr
        listing.summaryFr = result.summaryFr
        listing.descriptionFr = result.descriptionFr
        listing.heroImageId = if (result.heroImageIndex >= 0) images[result.heroImageIndex].id else null
        listing.publishedAt = Date()

        logger.add("success", true)
        logger.add("ai_agent", agent::class.java.simpleName)
        return listingService.save(listing)
    }
}
