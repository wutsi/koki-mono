package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class ListingLeadCreatedEventHandler(
    private val listingService: ListingService,
    private val leadService: LeadService,
    private val logger: KVLogger,
) {
    fun handle(event: LeadCreatedEvent) {
        logger.add("lead_id", event.leadId)
        logger.add("tenant_id", event.tenantId)

        val lead = leadService.get(event.leadId, event.tenantId)
        val listing = lead.listing
        if (listing != null) {
            listing.totalLeads = leadService.countByListingIdAndTenantId(lead.listing.id ?: -1L, event.tenantId).toInt()
            listingService.save(listing)
        }
    }
}
