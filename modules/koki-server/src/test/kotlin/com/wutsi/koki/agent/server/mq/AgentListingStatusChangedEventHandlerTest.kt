package com.wutsi.koki.agent.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Service
class AgentListingStatusChangedEventHandlerTest {
    private val agentService = mock<AgentService>()
    private val listingService = mock<ListingService>()
    private val logger = DefaultKVLogger()
    private val handler = AgentListingStatusChangedEventHandler(
        agentService = agentService,
        listingService = listingService,
        logger = logger,
    )

    private val listing = ListingEntity(
        soldAt = DateUtils.addDays(Date(), -2),
        sellerAgentUserId = 11L,
        buyerAgentUserId = 22,
    )
    private val agent11 = AgentEntity(userId = 11L)
    private val agent22 = AgentEntity(userId = 22L)

    @BeforeEach
    fun setUp() {
        reset(agentService)
        reset(listingService)
        agent11.lastSoldAt = null
        agent22.lastSoldAt = null

        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(agent11).whenever(agentService).getByUser(eq(11L), any())
        doReturn(agent22).whenever(agentService).getByUser(eq(22L), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun sold() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.SOLD))

        val agent = argumentCaptor<AgentEntity>()
        verify(agentService, times(2)).save(agent.capture())

        assertEquals(listing.sellerAgentUserId, agent.firstValue.userId)
        assertEquals(listing.soldAt, agent.firstValue.lastSoldAt)

        assertEquals(listing.buyerAgentUserId, agent.secondValue.userId)
        assertEquals(listing.soldAt, agent.secondValue.lastSoldAt)
    }

    @Test
    fun rented() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.RENTED))

        val agent = argumentCaptor<AgentEntity>()
        verify(agentService, times(2)).save(agent.capture())

        assertEquals(listing.sellerAgentUserId, agent.firstValue.userId)
        assertEquals(listing.soldAt, agent.firstValue.lastSoldAt)

        assertEquals(listing.buyerAgentUserId, agent.secondValue.userId)
        assertEquals(listing.soldAt, agent.secondValue.lastSoldAt)
    }

    @Test
    fun `seller and buyer are the same`() {
        doReturn(
            listing.copy(sellerAgentUserId = listing.buyerAgentUserId)
        ).whenever(listingService).get(any(), any())

        handler.handle(ListingStatusChangedEvent(status = ListingStatus.RENTED))

        val agent = argumentCaptor<AgentEntity>()
        verify(agentService).save(agent.capture())

        assertEquals(listing.buyerAgentUserId, agent.firstValue.userId)
        assertEquals(listing.soldAt, agent.firstValue.lastSoldAt)
    }

    @Test
    fun underContingencies() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.ACTIVE_WITH_CONTINGENCIES))
        verify(agentService, never()).save(any())
    }

    @Test
    fun expired() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.EXPIRED))
        verify(agentService, never()).save(any())
    }

    @Test
    fun withdrawn() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.WITHDRAWN))
        verify(agentService, never()).save(any())
    }

    @Test
    fun active() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.ACTIVE))
        verify(agentService, never()).save(any())
    }

    @Test
    fun pending() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.PENDING))
        verify(agentService, never()).save(any())
    }

    @Test
    fun publishing() {
        handler.handle(ListingStatusChangedEvent(status = ListingStatus.PUBLISHING))
        verify(agentService, never()).save(any())
    }
}
