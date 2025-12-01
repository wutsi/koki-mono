package com.wutsi.koki.listing.server.service.mq

import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.platform.mq.Consumer
import org.springframework.stereotype.Service

@Service
class ListingMQConsumer(
    private val fileUploadedEventHandler: ListingFileUploadedEventHandler,
    private val fileDeletedEventHandler: ListingFileDeletedEventHandler,
    private val listingStatusChangedEventHandler: ListingStatusChangedEventHandler,
    private val offerSubmittedEventHandler: ListingOfferSubmittedEventHandler,
    private val offerStatusChangedEventHandler: ListingOfferStatusChangedEventHandler,
    private val leadCreatedEventHandler: ListingLeadCreatedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is FileUploadedEvent) {
            fileUploadedEventHandler.handle(event)
        } else if (event is FileDeletedEvent) {
            fileDeletedEventHandler.handle(event)
        } else if (event is ListingStatusChangedEvent) {
            listingStatusChangedEventHandler.handle(event)
        } else if (event is OfferSubmittedEvent) {
            offerSubmittedEventHandler.handle(event)
        } else if (event is OfferStatusChangedEvent) {
            offerStatusChangedEventHandler.handle(event)
        } else if (event is LeadCreatedEvent) {
            leadCreatedEventHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
