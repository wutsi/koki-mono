package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingMQConsumerTest {
    private val fileUploadedEventHandler = mock<ListingFileUploadedEventHandler>()
    private val listingStatusChangedEventHandler = mock<ListingStatusChangedEventHandler>()
    private val consumer = ListingMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler,
        listingStatusChangedEventHandler = listingStatusChangedEventHandler,
    )

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler).handle(event)
        verify(listingStatusChangedEventHandler, never()).handle(any())
    }

    @Test
    fun statusChanged() {
        val event = ListingStatusChangedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(fileUploadedEventHandler, never()).handle(any())
        verify(listingStatusChangedEventHandler).handle(event)
    }

    @Test
    fun anyEvent() {
        val result = consumer.consume(mapOf("foo" to "bar"))

        assertEquals(false, result)
        verify(fileUploadedEventHandler, never()).handle(any())
    }
}
