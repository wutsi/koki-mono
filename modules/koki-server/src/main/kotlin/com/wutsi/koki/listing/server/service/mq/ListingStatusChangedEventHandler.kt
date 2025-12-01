package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service

@Service
class ListingStatusChangedEventHandler(
    private val listingPublisher: ListingPublisher,
    private val publisher: Publisher,
    private val logger: KVLogger,
) {
    fun handle(event: ListingStatusChangedEvent) {
        logger.add("event_status", event.status)
        logger.add("event_listing_id", event.listingId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status == ListingStatus.PUBLISHING) {
            val listing = listingPublisher.publish(event.listingId, event.tenantId)
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
}
