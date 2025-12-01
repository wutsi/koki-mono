package com.wutsi.koki.agent.server.service.mq

import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class AgentListingStatusChangedEventHandler(
    private val agentService: AgentService,
    private val listingService: ListingService,
    private val logger: KVLogger,
) {
    fun handle(event: ListingStatusChangedEvent) {
        logger.add("event_status", event.status)
        logger.add("event_listing_id", event.listingId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status == ListingStatus.RENTED || event.status == ListingStatus.SOLD) {
            onSold(event)
        }
    }

    private fun onSold(event: ListingStatusChangedEvent) {
        val listing = listingService.get(event.listingId, event.tenantId)

        listing.sellerAgentUserId?.let { userId -> updateMetric(userId, listing) }

        listing.buyerAgentUserId?.let { userId ->
            if (userId != listing.sellerAgentUserId) {
                updateMetric(userId, listing)
            }
        }
    }

    private fun updateMetric(userId: Long, listing: ListingEntity) {
        try {
            val agent = agentService.getByUser(userId, listing.tenantId)
            logger.add("agent_id", agent.id)

            agent.lastSoldAt = listing.soldAt
            agentService.save(agent)
        } catch (ex: NotFoundException) {
            // Ignore
        }
    }
}
