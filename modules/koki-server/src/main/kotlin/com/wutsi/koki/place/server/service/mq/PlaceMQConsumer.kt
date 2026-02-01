package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class PlaceMQConsumer(
    private val listingStatusChangedEventHandler: PlaceListingStatusChangedEventHandler,
    private val placeCreatedEventHandler: PlaceCreatedEventHandler,
    private val placeUpdatedEventHandler: UpdatePlaceEventHandler
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is ListingStatusChangedEvent) {
            try {
                return listingStatusChangedEventHandler.handle(event)
            } catch (ex: ConflictException) {
                if (ex.error.code == ErrorCode.PLACE_DUPLICATE_NAME) {
                    return false
                } else {
                    throw ex
                }
            }
        } else if (event is PlaceCreatedEvent) {
            return placeCreatedEventHandler.handle(event)
        } else if (event is PlaceUpdatedEvent) {
            return placeUpdatedEventHandler.handle(event)
        } else {
            return false
        }
    }
}
