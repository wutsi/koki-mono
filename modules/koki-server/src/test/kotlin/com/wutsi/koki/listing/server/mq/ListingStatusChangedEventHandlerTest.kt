package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.email.ListingClosedMailet
import com.wutsi.koki.platform.mq.Publisher
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingStatusChangedEventHandlerTest {
    private val publisher = mock<Publisher>()
    private val listingPublisher = mock<ListingPublisher>()
    private val listingClosedMailet = mock<ListingClosedMailet>()
    private val handler = ListingStatusChangedEventHandler(
        listingPublisher = listingPublisher,
        listingClosedMailet = listingClosedMailet,
        publisher = publisher,
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

        verify(listingClosedMailet, never()).service(any())
    }

    @Test
    fun `onPublishing - listing not published`() {
        doReturn(null).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.PUBLISHING)
        handler.handle(event)

        verify(listingPublisher).publish(event.listingId, event.tenantId)
        verify(publisher, never()).publish(any())
        verify(listingClosedMailet, never()).service(any())
    }

    @Test
    fun `onPublishing - unsupported event status`() {
        doReturn(null).whenever(listingPublisher).publish(any(), any())

        val event = createEvent(ListingStatus.ACTIVE)
        handler.handle(event)

        verify(listingPublisher, never()).publish(any(), any())
        verify(publisher, never()).publish(any())
        verify(listingClosedMailet, never()).service(any())
    }

    @Test
    fun onRented() {
        val event = createEvent(ListingStatus.RENTED)
        handler.handle(event)

        verify(listingPublisher, never()).publish(any(), any())
        verify(publisher, never()).publish(any())
        verify(listingClosedMailet).service(event)
    }

    @Test
    fun onSold() {
        val event = createEvent(ListingStatus.SOLD)
        handler.handle(event)

        verify(listingPublisher, never()).publish(any(), any())
        verify(publisher, never()).publish(any())
        verify(listingClosedMailet).service(event)
    }

    private fun createEvent(status: ListingStatus): ListingStatusChangedEvent {
        return ListingStatusChangedEvent(
            status = status,
            listingId = listing.id!!,
            tenantId = listing.tenantId
        )
    }
}
