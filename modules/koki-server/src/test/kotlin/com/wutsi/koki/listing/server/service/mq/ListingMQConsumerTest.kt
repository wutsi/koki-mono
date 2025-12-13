package com.wutsi.koki.listing.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMQConsumerTest {
    private val fileUploadedEventHandler = mock<ListingFileUploadedEventHandler>()
    private val fileDeletedEventHandler = mock<ListingFileDeletedEventHandler>()
    private val listingStatusChangedEventHandler = mock<ListingStatusChangedEventHandler>()
    private val offerSubmittedEventHandler = mock<ListingOfferSubmittedEventHandler>()
    private val offerStatusChangedEventHandler = mock<ListingOfferStatusChangedEventHandler>()
    private val leadMessageReceivedEventHandler = mock<ListingLeadMessageReceivedEventHandler>()
    private val consumer = ListingMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler,
        fileDeletedEventHandler = fileDeletedEventHandler,
        listingStatusChangedEventHandler = listingStatusChangedEventHandler,
        offerSubmittedEventHandler = offerSubmittedEventHandler,
        offerStatusChangedEventHandler = offerStatusChangedEventHandler,
        leadMessageReceivedEventHandler = leadMessageReceivedEventHandler
    )

    @BeforeEach
    fun setUp() {
        doReturn(true).whenever(fileUploadedEventHandler).handle(any())
        doReturn(true).whenever(fileDeletedEventHandler).handle(any())
        doReturn(true).whenever(listingStatusChangedEventHandler).handle(any())
        doReturn(true).whenever(offerSubmittedEventHandler).handle(any())
        doReturn(true).whenever(offerStatusChangedEventHandler).handle(any())
        doReturn(true).whenever(leadMessageReceivedEventHandler).handle(any())
    }

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler).handle(event)
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun fileDeleted() {
        val event = FileDeletedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileDeletedEventHandler).handle(event)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun listingStatusChanged() {
        val event = ListingStatusChangedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler).handle(event)
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun offerSubmitted() {
        val event = OfferSubmittedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler).handle(event)
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun offerStatusChanged() {
        val event = OfferStatusChangedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler).handle(event)
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun leadCreatedEvent() {
        val event = LeadMessageReceivedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler).handle(event)
    }

    @Test
    fun anyEvent() {
        val result = consumer.consume(mapOf("foo" to "bar"))

        assertEquals(false, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
        verify(offerSubmittedEventHandler, never()).handle(any())
        verify(offerStatusChangedEventHandler, never()).handle(any())
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }
}
