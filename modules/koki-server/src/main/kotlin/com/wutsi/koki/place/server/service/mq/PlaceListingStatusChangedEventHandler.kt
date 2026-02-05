package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Service

@Service
class PlaceListingStatusChangedEventHandler(
    private val listingService: ListingService,
    private val worker: PlaceContentGeneratorWorker,
    private val logger: KVLogger,
) {
    fun handle(event: ListingStatusChangedEvent): Boolean {
        logger.add("event_status", event.status)
        logger.add("event_listing_id", event.listingId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status != ListingStatus.ACTIVE) {
            return false
        }

        val listing = listingService.get(event.listingId, event.tenantId)
        listing.neighbourhoodId?.let { id -> worker.generate(id, LocationType.NEIGHBORHOOD) }
        listing.cityId?.let { id -> worker.generate(id, LocationType.CITY) }
        return true
    }
}
