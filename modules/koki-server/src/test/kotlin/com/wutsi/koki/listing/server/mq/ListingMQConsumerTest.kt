package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMQConsumerTest {
    private val fileUploadedEventHandler = mock<ListingFileUploadedEventHandler>()
    private val fileDeletedEventHandler = mock<ListingFileDeletedEventHandler>()
    private val listingStatusChangedEventHandler = mock<ListingStatusChangedEventHandler>()
    private val consumer = ListingMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler,
        fileDeletedEventHandler = fileDeletedEventHandler,
        listingStatusChangedEventHandler = listingStatusChangedEventHandler,
    )

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler).handle(event)
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
    }

    @Test
    fun fileDeleted() {
        val event = FileDeletedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileDeletedEventHandler).handle(event)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler, never()).handle(any())
    }

    @Test
    fun statusChanged() {
        val event = ListingStatusChangedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(fileDeletedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler).handle(event)
    }

    @Test
    fun anyEvent() {
        val result = consumer.consume(mapOf("foo" to "bar"))

        assertEquals(false, result)
        verify(fileUploadedEventHandler, never()).handle(any())
    }
}
