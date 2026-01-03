package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.ai.llm.LLMException
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaceListingStatusChangedEventHandlerTest {
    private val locationService = mock<LocationService>()
    private val placeService = mock<PlaceService>()
    private val listingService = mock<ListingService>()
    private val logger = DefaultKVLogger()

    private val handler = PlaceListingStatusChangedEventHandler(
        locationService = locationService,
        placeService = placeService,
        listingService = listingService,
        logger = logger,
    )

    private val tenantId = 1L
    private val listingId = 100L
    private val neighbourhoodId = 200L
    private val cityId = 300L
    private val placeId = 400L

    private val neighbourhood = LocationEntity(
        id = neighbourhoodId,
        name = "Downtown",
        type = LocationType.NEIGHBORHOOD,
        parentId = cityId,
        country = "CA",
    )

    private val listing = ListingEntity(
        id = listingId,
        tenantId = tenantId,
        neighbourhoodId = neighbourhoodId,
    )

    private val place = PlaceEntity(
        id = placeId,
        neighbourhoodId = neighbourhoodId,
        type = PlaceType.NEIGHBORHOOD,
        name = "Downtown",
        asciiName = "downtown",
        summary = null,
        introduction = null,
        description = null,
    )

    private val placeWithContent = PlaceEntity(
        id = placeId,
        neighbourhoodId = neighbourhoodId,
        type = PlaceType.NEIGHBORHOOD,
        name = "Downtown",
        asciiName = "downtown",
        summary = "A vibrant downtown area",
        introduction = "Welcome to downtown",
        description = "Full description of the area",
    )

    @BeforeEach
    fun setUp() {
        reset(locationService, placeService, listingService)

        doReturn(listing).whenever(listingService).get(eq(listingId), eq(tenantId))
        doReturn(neighbourhood).whenever(locationService).get(eq(neighbourhoodId), eq(LocationType.NEIGHBORHOOD))
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `should return false when LLMException is thrown`() {
        // Given
        doThrow(LLMException::class).whenever(placeService).update(any())
        doReturn(listOf(place)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return true and generate content when listing is active and place has no content`() {
        // Given
        doReturn(listOf(place)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService).update(place.id!!)
    }

    @Test
    fun `should return true but not generate content when listing is active and place already has content`() {
        // Given
        doReturn(listOf(placeWithContent)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return true and create neighbourhood place when place does not exist`() {
        // Given
        doReturn(emptyList<PlaceEntity>()).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )
        doReturn(place).whenever(placeService).create(any())

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)

        val requestCaptor = argumentCaptor<CreatePlaceRequest>()
        verify(placeService).create(requestCaptor.capture())

        val capturedRequest = requestCaptor.firstValue
        assertEquals("Downtown", capturedRequest.name)
        assertEquals(neighbourhoodId, capturedRequest.neighbourhoodId)
        assertEquals(PlaceType.NEIGHBORHOOD, capturedRequest.type)
        assertFalse(capturedRequest.generateContent)

        verify(placeService).update(placeId)
    }

    @Test
    fun `should return false and do nothing when listing has no neighbourhood`() {
        // Given
        doReturn(listing.copy(neighbourhoodId = null)).whenever(listingService).get(any(), any())

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(listingService).get(listingId, tenantId)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return false when listing status is not active`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.PENDING
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return false for SOLD status`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.SOLD
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return false for RENTED status`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.RENTED
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return false for EXPIRED status`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.EXPIRED
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should return false for WITHDRAWN status`() {
        // Given
        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.WITHDRAWN
        )

        // When
        val result = handler.handle(event)

        // Then
        assertFalse(result)
        verify(placeService, never()).create(any())
        verify(placeService, never()).update(any())
    }

    @Test
    fun `should handle place with partial content - missing summary`() {
        // Given
        val placePartialContent = place.copy(
            introduction = "Intro",
            description = "Desc",
            summary = null
        )
        doReturn(listOf(placePartialContent)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService).update(placeId)
    }

    @Test
    fun `should handle place with partial content - missing introduction`() {
        // Given
        val placePartialContent = place.copy(
            summary = "Summary",
            description = "Desc",
            introduction = null
        )
        doReturn(listOf(placePartialContent)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()

        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService).update(placeId)
    }

    @Test
    fun `should handle place with partial content - missing description`() {
        // Given
        val placePartialContent = place.copy(
            summary = "Summary",
            introduction = "Intro",
            description = null
        )
        doReturn(listOf(placePartialContent)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService).update(placeId)
    }

    @Test
    fun `should handle place with empty strings as missing content`() {
        // Given
        val placeEmptyContent = place.copy(
            summary = "",
            introduction = "",
            description = ""
        )
        doReturn(listOf(placeEmptyContent)).whenever(placeService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )

        val event = ListingStatusChangedEvent(
            listingId = listingId,
            tenantId = tenantId,
            status = ListingStatus.ACTIVE
        )

        // When
        val result = handler.handle(event)

        // Then
        assertTrue(result)
        verify(placeService, never()).create(any())
        verify(placeService).update(placeId)
    }
}
