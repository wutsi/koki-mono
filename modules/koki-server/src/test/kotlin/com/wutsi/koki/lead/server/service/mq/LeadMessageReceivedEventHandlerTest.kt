package com.wutsi.koki.lead.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.domain.LeadEntity
import com.wutsi.koki.lead.server.domain.LeadMessageEntity
import com.wutsi.koki.lead.server.service.LeadMessageService
import com.wutsi.koki.lead.server.service.LeadService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class LeadMessageReceivedEventHandlerTest {
    private val leadMessageService = mock<LeadMessageService>()
    private val leadService = mock<LeadService>()
    private val logger = DefaultKVLogger()
    private val handler = LeadMessageReceivedEventHandler(
        leadMessageService = leadMessageService,
        leadService = leadService,
        logger = logger,
    )

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
    )

    private val lead = LeadEntity(
        id = 555L,
        listing = listing,
        tenantId = tenantId,
        totalMessages = 0,
    )

    private val message = LeadMessageEntity(
        id = 111L,
        lead = lead,
        tenantId = tenantId,
    )

    @BeforeEach
    fun setUp() {
        doReturn(message).whenever(leadMessageService).get(any(), any())
        doReturn(5L).whenever(leadMessageService).countByLead(any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `updates total messages count on lead when message received`() {
        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = false,
        )
        handler.handle(event)

        val argLead = argumentCaptor<LeadEntity>()
        verify(leadService).save(argLead.capture())
        assertEquals(5, argLead.firstValue.totalMessages)
    }

    @Test
    fun `updates total messages count on lead when new lead created`() {
        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = true,
        )
        handler.handle(event)

        val argLead = argumentCaptor<LeadEntity>()
        verify(leadService).save(argLead.capture())
        assertEquals(5, argLead.firstValue.totalMessages)
    }

    @Test
    fun `throws exception when message not found`() {
        doThrow(NotFoundException(Error(ErrorCode.MESSAGE_NOT_FOUND)))
            .whenever(leadMessageService).get(any(), any())

        val event = LeadMessageReceivedEvent(
            messageId = 999L,
            tenantId = tenantId,
            newLead = false,
        )

        assertThrows<NotFoundException> {
            handler.handle(event)
        }

        verify(leadService, never()).save(any())
    }

    @Test
    fun `updates total messages to zero when no messages exist`() {
        doReturn(0L).whenever(leadMessageService).countByLead(any())

        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = false,
        )
        handler.handle(event)

        val argLead = argumentCaptor<LeadEntity>()
        verify(leadService).save(argLead.capture())
        assertEquals(0, argLead.firstValue.totalMessages)
    }

    @Test
    fun `retrieves message with correct tenant id`() {
        val event = LeadMessageReceivedEvent(
            messageId = message.id!!,
            tenantId = tenantId,
            newLead = false,
        )
        handler.handle(event)

        verify(leadMessageService).get(message.id!!, tenantId)
    }
}
