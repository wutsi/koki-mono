package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service

@Service
class ListingStatusChangedEventHandler(
    private val listingPublisher: ListingPublisher,
    private val publisher: Publisher,
) {
    fun handle(event: ListingStatusChangedEvent) {
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
