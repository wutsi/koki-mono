package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingOfferSubmittedEventHandlerTest {
    private val offerService = mock<OfferService>()
    private val listingService = mock<ListingService>()
    private val logger = DefaultKVLogger()
    private val handler = ListingOfferSubmittedEventHandler(offerService, listingService, logger)

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        totalOffers = null,
        status = ListingStatus.PUBLISHING
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(listing).whenever(listingService).save(any(), anyOrNull())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun handle() {
        doReturn(77).whenever(offerService).countByOwnerIdAndOwnerTypeAndTenantId(any(), any(), any())

        val event = OfferSubmittedEvent(
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            offerId = 333,
            tenantId = -1,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(77, argListing.firstValue.totalOffers)
    }

    @Test
    fun notListingEvent() {
        doReturn(77).whenever(offerService).countByOwnerIdAndOwnerTypeAndTenantId(any(), any(), any())

        val event = OfferSubmittedEvent(
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.ACCOUNT),
            offerId = 333,
            tenantId = -1,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }
}
