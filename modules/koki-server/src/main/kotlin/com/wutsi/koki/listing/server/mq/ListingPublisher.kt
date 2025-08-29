package com.wutsi.koki.listing.server.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.agent.ListingAgentFactory
import com.wutsi.koki.listing.server.service.agent.ListingDescriptorAgent
import com.wutsi.koki.listing.server.service.agent.ListingDescriptorAgentResult
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ListingStatusChangedEventHandler(
    private val listingService: ListingService,
    private val agentFactory: ListingAgentFactory,
    private val fileService: FileService,
    private val objectMapper: ObjectMapper,
    private val publisher: Publisher,
    private val logger: KVLogger
) {
    fun handle(event: ListingStatusChangedEvent) {
        if (event.status == ListingStatus.PUBLISHING) {
            val listing = publish(event)
            if (listing?.status == ListingStatus.ACTIVE) {
                publisher.publish(
                    ListingStatusChangedEvent(
                        listingId = event.listingId,
                        tenantId = event.tenantId,
                        status = listing.status,
                    )
                )
            }
        }
    }

    private fun publish(event: ListingStatusChangedEvent): ListingEntity? {
        val listing = listingService.get(event.listingId, event.tenantId)
        if (listing.status != ListingStatus.PUBLISHING) {
            logger.add("success", false)
            logger.add("error", "Invalid status")
            logger.add("listing_status", listing.status)
            return null
        }

        val agent = agentFactory.createDescriptorAgent(listing, event.tenantId)
        val images = fileService.search(
            tenantId = event.tenantId,
            ownerId = listing.id,
            ownerType = ObjectType.LISTING,
            status = FileStatus.APPROVED,
            type = FileType.IMAGE,
            limit = Integer.MAX_VALUE,
        )
        val files = images.map { file -> fileService.download(file) }
        val json = agent.run(ListingDescriptorAgent.QUERY, files)
        val result = objectMapper.readValue(json, ListingDescriptorAgentResult::class.java)
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
