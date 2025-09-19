package com.wutsi.koki.listing.server.mq

import com.wutsi.koki.common.dto.ObjectType
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
class OfferStatusChangedEventHandler(
    private val offerService: OfferService,
    private val listingService: ListingService,
    private val logger: KVLogger,
    private val publisher: Publisher,
) {
    fun handle(event: OfferStatusChangedEvent) {
        if (!accept(event)) {
            return
        }

        logger.add("event", event::class.java.simpleName)
        logger.add("event_status", event.status)
        logger.add("event_offer_id", event.offerId)
        logger.add("event_owner_id", event.owner?.id)
        logger.add("event_owner_type", event.owner?.type)

        val offer = offerService.get(event.offerId, event.tenantId)
        if (offer.status != event.status) {
            logger.add("offer_status", offer.status)
            logger.add("ignored", true)
            logger.add("reason", "Offer/Event status mismatch")
            return
        }

        when (event.status) {
            OfferStatus.ACCEPTED -> offerAccepted(event)
            OfferStatus.WITHDRAWN -> offerWithdrawn(event)
            OfferStatus.CLOSED -> offerClosed(event, offer)
            else -> {}
        }
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
        val listing = listingService.get(event.owner!!.id, event.tenantId)
        logger.add("listing_status", listing.status)

        if (listing.status == ListingStatus.PENDING) {
            // Update the listing
            val finalPrice = offer.version!!.price
            listing.status = if (listing.listingType == ListingType.RENTAL) ListingStatus.RENTED else ListingStatus.SOLD
            listing.closedOfferId = offer.id
            listing.buyerAgentUserId = offer.buyerAgentUserId
            listing.buyerContactId = offer.buyerContactId
            listing.transactionDate = Date(event.timestamp)
            listing.transactionPrice = finalPrice
            listing.finalSellerAgentCommissionAmount = computeCommission(finalPrice, listing.sellerAgentCommission)
            listing.finalBuyerAgentCommissionAmount = if (offer.buyerAgentUserId != listing.sellerAgentUserId) {
                computeCommission(finalPrice, listing.buyerAgentCommission)
            } else {
                null
            }

            logger.add("listing_new_status", listing.status)
            save(listing)
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

    private fun computeCommission(price: Long?, percent: Double?): Long {
        return ((price ?: 0) * (percent ?: 0.0) / 100.0).toLong()
    }

    private fun hasOffersOfStatus(status: OfferStatus, tenantId: Long): Boolean {
        return offerService.search(
            tenantId = tenantId,
            statuses = listOf(status)
        ).isNotEmpty()
    }
}
