package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class PlaceMQConsumer(
    private val listingStatusChangedEventHandler: PlaceListingStatusChangedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is ListingStatusChangedEvent) {
            return listingStatusChangedEventHandler.handle(event)
        } else {
            return false
        }
    }
}
