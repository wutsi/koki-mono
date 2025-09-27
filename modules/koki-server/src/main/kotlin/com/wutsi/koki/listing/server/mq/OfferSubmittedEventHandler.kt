package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class OfferSubmittedEventHandler(
    private val offerService: OfferService,
    private val listingService: ListingService,
    private val logger: KVLogger,
) {
    fun handle(event: OfferSubmittedEvent) {
        logger.add("event_offer_id", event.offerId)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        if (!accept(event)) {
            return
        }

        val listing = listingService.get(event.owner!!.id, event.tenantId)
        listing.totalOffers = offerService.countByOwnerIdAndOwnerTypeAndTenantId(
            listing.id ?: -1,
            ObjectType.LISTING,
            event.tenantId,
        )
        listingService.save(listing)
        logger.add("listing_total_offers", listing.totalOffers)
    }

    private fun accept(event: OfferSubmittedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }
}
