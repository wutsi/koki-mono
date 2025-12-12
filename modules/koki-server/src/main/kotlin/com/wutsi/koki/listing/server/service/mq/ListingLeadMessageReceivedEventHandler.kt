package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class ListingLeadMessageReceivedEventHandler(
    private val listingService: ListingService,
    private val leadMessageService: LeadMessageService,
    private val leadService: LeadService,
    private val logger: KVLogger,
) {
    fun handle(event: LeadMessageReceivedEvent): Boolean {
        logger.add("message_id", event.messageId)
        logger.add("tenant_id", event.tenantId)
        logger.add("new_lead", event.newLead)

        if (!event.newLead) {
            return false
        }

        val message = leadMessageService.get(event.messageId, event.tenantId)
        val listing = message.lead.listing
        if (listing != null) {
            listing.totalLeads =
                leadService.countByListingIdAndTenantId(message.lead.listing.id ?: -1L, event.tenantId).toInt()
            listingService.save(listing)
            return true
        }
        return false
    }
}
