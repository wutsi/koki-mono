package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaceMQConsumerTest {
    private val listingStatusChangedEventHandler = mock<PlaceListingStatusChangedEventHandler>()

    private val consumer = PlaceMQConsumer(
        listingStatusChangedEventHandler = listingStatusChangedEventHandler,
    )

    @Test
    fun `should consume ListingStatusChangedEvent and return true when handler returns true`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = 100L,
            tenantId = 1L,
            status = ListingStatus.ACTIVE
        )
        doReturn(true).whenever(listingStatusChangedEventHandler).handle(event)

        // When
        val result = consumer.consume(event)

        // Then
        assertTrue(result)
        verify(listingStatusChangedEventHandler).handle(event)
    }

    @Test
    fun `should consume ListingStatusChangedEvent and return false when handler returns false`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = 100L,
            tenantId = 1L,
            status = ListingStatus.PENDING
        )
        doReturn(false).whenever(listingStatusChangedEventHandler).handle(event)

        // When
        val result = consumer.consume(event)

        // Then
        assertFalse(result)
        verify(listingStatusChangedEventHandler).handle(event)
    }

    @Test
    fun `should return false for unsupported event type - Any object`() {
        // Given
        val event = Any()

        // When
        val result = consumer.consume(event)

        // Then
        assertFalse(result)
        verify(listingStatusChangedEventHandler, never()).handle(any())
    }
}
