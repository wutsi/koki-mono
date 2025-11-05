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
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.server.domain.OfferEntity
import com.wutsi.koki.offer.server.domain.OfferVersionEntity
import com.wutsi.koki.offer.server.service.OfferService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class OfferStatusChangedEventHandlerTest {
    private val offerService = mock<OfferService>()
    private val listingService = mock<ListingService>()
    private val publisher = mock<Publisher>()
    private val logger = DefaultKVLogger()
    private val handler = OfferStatusChangedEventHandler(
        offerService = offerService,
        listingService = listingService,
        publisher = publisher,
        logger = logger,
    )

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        totalOffers = null,
        status = ListingStatus.ACTIVE,
        sellerAgentUserId = 11L,
        sellerAgentCommission = 5.0,
        buyerAgentCommission = 2.5,
        price = 90000L,
        currency = "CAD",
    )

    private val offer = OfferEntity(
        id = 555L,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        status = OfferStatus.SUBMITTED,
        buyerAgentUserId = 33L,
        sellerAgentUserId = 55L,
        version = OfferVersionEntity(
            status = OfferStatus.SUBMITTED,
            price = 100000L,
            currency = "CAD",
        ),
        closedAt = DateUtils.addDays(Date(), -1)
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(listing).whenever(listingService).save(any(), anyOrNull())

        doReturn(offer).whenever(offerService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `offer accepted - active`() {
        doReturn(listing.copy(status = ListingStatus.ACTIVE)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.ACCEPTED)).whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.ACCEPTED,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.PENDING, argListing.firstValue.status)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer accepted - active-with-contingencies`() {
        doReturn(listing.copy(status = ListingStatus.ACTIVE_WITH_CONTINGENCIES)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.ACCEPTED)).whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.ACCEPTED,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.PENDING, argListing.firstValue.status)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer accepted - not-active`() {
        doReturn(listing.copy(status = ListingStatus.EXPIRED)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.ACCEPTED)).whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.ACCEPTED,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `offer accepted - status mismatch`() {
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN)).whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.ACCEPTED,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `offer closed - rental`() {
        doReturn(listing.copy(listingType = ListingType.RENTAL, status = ListingStatus.PENDING))
            .whenever(listingService).get(any(), any())

        doReturn(offer.copy(status = OfferStatus.CLOSED))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.CLOSED,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.RENTED, argListing.firstValue.status)
        assertEquals(offer.id, argListing.firstValue.closedOfferId)
        assertEquals(offer.buyerAgentUserId, argListing.firstValue.buyerAgentUserId)
        assertEquals(offer.buyerContactId, argListing.firstValue.buyerContactId)
        assertEquals(offer.closedAt, argListing.firstValue.soldAt)
        assertEquals(offer.version?.price, argListing.firstValue.salePrice)
        assertEquals(5000, argListing.firstValue.finalSellerAgentCommissionAmount)
        assertEquals(2500, argListing.firstValue.finalBuyerAgentCommissionAmount)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer closed - sale`() {
        doReturn(listing.copy(listingType = ListingType.SALE, status = ListingStatus.PENDING))
            .whenever(listingService).get(any(), any())

        doReturn(offer.copy(status = OfferStatus.CLOSED, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.CLOSED,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.SOLD, argListing.firstValue.status)
        assertEquals(offer.id, argListing.firstValue.closedOfferId)
        assertEquals(listing.sellerAgentUserId, argListing.firstValue.buyerAgentUserId)
        assertEquals(offer.buyerContactId, argListing.firstValue.buyerContactId)
        assertEquals(offer.closedAt, argListing.firstValue.soldAt)
        assertEquals(offer.version?.price, argListing.firstValue.salePrice)
        assertEquals(5000, argListing.firstValue.finalSellerAgentCommissionAmount)
        assertEquals(null, argListing.firstValue.finalBuyerAgentCommissionAmount)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer closed - not-pending`() {
        doReturn(listing.copy(status = ListingStatus.ACTIVE_WITH_CONTINGENCIES)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.CLOSED, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.CLOSED,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `offer PENDING withdrawn`() {
        doReturnOffers(emptyList())
        doReturn(listing.copy(status = ListingStatus.PENDING)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.WITHDRAWN,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.ACTIVE, argListing.firstValue.status)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer ACTIVE_WITH_CONTENGENCY withdrawn`() {
        doReturnOffers(emptyList())
        doReturn(listing.copy(status = ListingStatus.ACTIVE_WITH_CONTINGENCIES)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.WITHDRAWN,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.ACTIVE, argListing.firstValue.status)
    }

    @Test
    fun `offer SOLD withdrawn`() {
        doReturnOffers(emptyList())
        doReturn(listing.copy(status = ListingStatus.SOLD)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.WITHDRAWN,
        )
        handler.handle(event)

        var argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(ListingStatus.ACTIVE, argListing.firstValue.status)

        val argEvent = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(argEvent.capture())
        assertEquals(argListing.firstValue.status, argEvent.firstValue.status)
        assertEquals(argListing.firstValue.id, argEvent.firstValue.listingId)
        assertEquals(tenantId, argEvent.firstValue.tenantId)
    }

    @Test
    fun `offer SOLD withdrawn - pending offers`() {
        doReturnOffers(listOf(OfferEntity(), OfferEntity()))
        doReturn(listing.copy(status = ListingStatus.SOLD)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.WITHDRAWN,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `offer EXPIRED withdrawn`() {
        doReturnOffers(emptyList())
        doReturn(listing.copy(status = ListingStatus.EXPIRED)).whenever(listingService)
            .get(any(), any())
        doReturn(offer.copy(status = OfferStatus.WITHDRAWN, buyerAgentUserId = listing.sellerAgentUserId!!))
            .whenever(offerService).get(any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = listing.id ?: -1, type = ObjectType.LISTING),
            status = OfferStatus.WITHDRAWN,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `not listing event`() {
        doReturn(77).whenever(offerService).countByOwnerIdAndOwnerTypeAndTenantId(any(), any(), any())

        val event = OfferStatusChangedEvent(
            offerId = offer.id!!,
            tenantId = tenantId,
            owner = ObjectReference(id = 555, type = ObjectType.ACCOUNT),
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
        verify(publisher, never()).publish(any())
    }

    fun doReturnOffers(offers: List<OfferEntity>) {
        doReturn(offers)
            .whenever(offerService)
            .search(
                anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
                anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
                anyOrNull(),
            )
    }
}
