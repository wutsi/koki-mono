package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.WutsiException
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class PlaceMQConsumer(
    private val listingStatusChangedEventHandler: PlaceListingStatusChangedEventHandler,
    private val placeCreatedEventHandler: PlaceCreatedEventHandler,
    private val placeUpdatedEventHandler: UpdatePlaceEventHandler,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        try {
            if (event is ListingStatusChangedEvent) {
                return listingStatusChangedEventHandler.handle(event)
            } else if (event is PlaceCreatedEvent) {
                return placeCreatedEventHandler.handle(event)
            } else if (event is PlaceUpdatedEvent) {
                return placeUpdatedEventHandler.handle(event)
            } else {
                return false
            }
        } catch (ex: WutsiException) {
            when (ex.error.code) {
                ErrorCode.PLACE_DUPLICATE_NAME,
                ErrorCode.PLACE_NOT_FOUND -> {
                    logger.add("warning", ex.error.code)
                    return false
                }

                else -> throw ex
            }
        }
    }
}
