package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ListingOfferStatusChangedEventHandler(
    private val offerService: OfferService,
    private val listingService: ListingService,
    private val logger: KVLogger,
    private val publisher: Publisher,
) {
    fun handle(event: OfferStatusChangedEvent): Boolean {
        logger.add("event_status", event.status)
        logger.add("event_offer_id", event.offerId)
        logger.add("event_tenant_id", event.tenantId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        if (!accept(event)) {
            return false
        }

        val offer = offerService.get(event.offerId, event.tenantId)
        if (offer.status != event.status) {
            logger.add("offer_status", offer.status)
            logger.add("ignored", true)
            logger.add("reason", "Offer/Event status mismatch")
            return false
        }

        when (event.status) {
            OfferStatus.ACCEPTED -> offerAccepted(event)
            OfferStatus.WITHDRAWN -> offerWithdrawn(event)
            OfferStatus.CLOSED -> offerClosed(event, offer)
            else -> return false
        }
        return true
    }

    private fun offerAccepted(event: OfferStatusChangedEvent) {
        val listing = listingService.get(event.owner!!.id, event.tenantId)
        logger.add("listing_status", listing.status)

        if (listing.status == ListingStatus.ACTIVE || listing.status == ListingStatus.ACTIVE_WITH_CONTINGENCIES) {
            listing.status = ListingStatus.PENDING

            logger.add("listing_new_status", listing.status)
            save(listing)
        } else {
            logger.add("ignored", true)
        }
    }

    private fun offerWithdrawn(event: OfferStatusChangedEvent) {
        val listing = listingService.get(event.owner!!.id, event.tenantId)
        logger.add("listing_status", listing.status)

        var changeStatus: Boolean = false
        if (listing.status == ListingStatus.ACTIVE_WITH_CONTINGENCIES || listing.status == ListingStatus.PENDING) {
            changeStatus = !hasOffersOfStatus(OfferStatus.ACCEPTED, event.tenantId)
        } else if (listing.status == ListingStatus.RENTED || listing.status == ListingStatus.SOLD) {
            changeStatus = !hasOffersOfStatus(OfferStatus.CLOSED, event.tenantId)
        }

        if (changeStatus) {
            listing.status = ListingStatus.ACTIVE

            logger.add("listing_new_status", listing.status)
            save(listing)
        }
        logger.add("ignored", !changeStatus)
    }

    private fun offerClosed(event: OfferStatusChangedEvent, offer: OfferEntity) {
        val listing = listingService.get(event.owner?.id ?: -1, event.tenantId)
        logger.add("listing_status", listing.status)

        if (listing.status == ListingStatus.PENDING) {
            // Update the listing
            val finalPrice = offer.version!!.price
            val xlisting = listingService.close(
                id = event.owner!!.id,
                tenantId = event.tenantId,
                request = CloseListingRequest(
                    status = if (listing.listingType == ListingType.RENTAL) ListingStatus.RENTED else ListingStatus.SOLD,
                    closedOfferId = offer.id,
                    buyerAgentUserId = offer.buyerAgentUserId,
                    buyerContactId = offer.buyerContactId,
                    soldAt = offer.closedAt ?: Date(event.timestamp),
                    salePrice = finalPrice,
                )
            )

            logger.add("listing_new_status", xlisting.status)
            publisher.publish(
                ListingStatusChangedEvent(
                    status = xlisting.status,
                    listingId = event.owner?.id ?: -1,
                    tenantId = event.tenantId,
                )
            )
        } else {
            logger.add("ignored", true)
        }
    }

    private fun save(listing: ListingEntity) {
        listingService.save(listing)
        publisher.publish(
            ListingStatusChangedEvent(
                status = listing.status,
                listingId = listing.id ?: -1,
                tenantId = listing.tenantId,
            )
        )
    }

    private fun accept(event: OfferStatusChangedEvent): Boolean {
        return event.owner?.type == ObjectType.LISTING
    }

    private fun hasOffersOfStatus(status: OfferStatus, tenantId: Long): Boolean {
        return offerService.search(
            tenantId = tenantId,
            statuses = listOf(status)
        ).isNotEmpty()
    }
}
