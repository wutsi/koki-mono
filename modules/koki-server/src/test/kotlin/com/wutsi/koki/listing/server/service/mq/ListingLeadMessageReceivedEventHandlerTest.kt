package com.wutsi.koki.listing.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingLeadMessageReceivedEventHandlerTest {
    private val leadService = mock<LeadService>()
    private val listingService = mock<ListingService>()
    private val leadMessageService = mock<LeadMessageService>()
    private val logger = DefaultKVLogger()
    private val handler = ListingLeadMessageReceivedEventHandler(
        leadService = leadService,
        listingService = listingService,
        leadMessageService = leadMessageService,
        logger = logger,
    )

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
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

    private val message = LeadMessageEntity(
        id = 333,
        lead = lead,
        tenantId = tenantId,
    )

    @BeforeEach
    fun setUp() {
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(listing).whenever(listingService).save(any(), anyOrNull())
        doReturn(message).whenever(leadMessageService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `lead created`() {
        doReturn(33L).whenever(leadService).countByListingIdAndTenantId(any(), any())

        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = true,
        )
        handler.handle(event)

        val argListing = argumentCaptor<ListingEntity>()
        verify(listingService).save(argListing.capture(), anyOrNull())
        assertEquals(33, argListing.firstValue.totalLeads)
    }

    @Test
    fun `follow up message`() {
        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = false,
        )
        handler.handle(event)

        verify(listingService, never()).save(any(), any())
    }
}
