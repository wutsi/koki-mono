package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class OfferStatusEventHandler(
    private val offerService: OfferService,
    private val listingService: ListingService,
    private val logger: KVLogger,
) {
    fun handle(event: OfferStatusChangedEvent) {
        if (!accept(event)) {
            return
        }

        val offer = offerService.get(event.offerId, event.tenantId)
        if (offer.status != event.status) {
            return
        }

        when (event.status) {
            OfferStatus.ACCEPTED -> offerAccepted(event)
            else -> {}
        }
    }

    private fun offerAccepted(event: OfferStatusChangedEvent) {
        val listing = listingService.get(event.owner!!.id, event.tenantId)
        listing.status = ListingStatus.ACTIVE_WITH_CONTINGENCIES
        listingService.save(listing)

        logger.add("listing_id", listing.id)
        logger.add("listing_status", listing.status)
    }

    private fun accept(event: OfferStatusChangedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }
}
