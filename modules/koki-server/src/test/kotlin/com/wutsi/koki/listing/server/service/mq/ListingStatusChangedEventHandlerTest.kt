package com.wutsi.koki.listing.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingPublisher
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.AfterEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListingStatusChangedEventHandlerTest {
    private val publisher = mock<Publisher>()
    private val listingPublisher = mock<ListingPublisher>()
    private val logger = DefaultKVLogger()
    private val listingService = mock<ListingService>()
    private val handler = ListingStatusChangedEventHandler(
        listingPublisher = listingPublisher,
        logger = logger,
        publisher = publisher,
        listingService = listingService,
    )

    private val tenantId = 1L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        heroImageId = null,
        totalImages = null,
        totalFiles = null,
        status = ListingStatus.ACTIVE
    )

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onPublishing() {
        doReturn(listing).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.PUBLISHING)
        handler.handle(event)

        verify(listingPublisher).publish(event.listingId, event.tenantId)

        val eventArg = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(eventArg.capture())
        assertEquals(ListingStatus.ACTIVE, eventArg.firstValue.status)
        assertEquals(listing.id, eventArg.firstValue.listingId)
        assertEquals(listing.tenantId, eventArg.firstValue.tenantId)
    }

    @Test
    fun `onPublishing - listing not published`() {
        doReturn(null).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.PUBLISHING)
        handler.handle(event)

        verify(listingPublisher).publish(event.listingId, event.tenantId)
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `unsupported event status`() {
        doReturn(null).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.CANCELLED)
        val result = handler.handle(event)

        verify(listingPublisher, never()).publish(any(), any())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun onActive() {
        doReturn(null).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.ACTIVE)
        val result = handler.handle(event)

        assertTrue(result)
        verify(listingService).generateQrCode(event.listingId, event.tenantId)
        verify(publisher, never()).publish(any())
    }

    private fun createEvent(status: ListingStatus): ListingStatusChangedEvent {
        return ListingStatusChangedEvent(
            status = status,
            listingId = listing.id!!,
            tenantId = listing.tenantId
        )
    }
}
