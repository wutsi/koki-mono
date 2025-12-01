package com.wutsi.koki.listing.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.mq.ListingLeadCreatedEventHandler
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingLeadCreatedEventHandlerTest {
    private val leadService = mock<LeadService>()
    private val listingService = mock<ListingService>()
    private val logger = DefaultKVLogger()
    private val handler = ListingLeadCreatedEventHandler(
        leadService = leadService,
        listingService = listingService,
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

    private val lead = LeadEntity(
        id = 555L,
        listing = listing,
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(listing).whenever(listingService).save(any(), anyOrNull())

        doReturn(lead).whenever(leadService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `lead created`() {
        doReturn(33L).whenever(leadService).countByListingIdAndTenantId(any(), any())

        val event = LeadCreatedEvent(
            leadId = lead.id!!,
            tenantId = tenantId,
        )
        handler.handle(event)

        val argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(33, argListing.firstValue.totalLeads)
    }
}
