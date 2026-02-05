package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class PlaceContentGeneratorWorkerTest {
    private val locationService = mock<LocationService>()
    private val placeService = mock<PlaceService>()
    private val placeCreatedEventHandler = mock<PlaceCreatedEventHandler>()
    private val placeUpdatePlaceEventHandler = mock<UpdatePlaceEventHandler>()

    private val worker = PlaceContentGeneratorWorker(
        locationService = locationService,
        placeService = placeService,
        placeCreatedEventHandler = placeCreatedEventHandler,
        placeUpdatePlaceEventHandler = placeUpdatePlaceEventHandler,
    )

    private val city = LocationEntity(
        id = 300,
        name = "Montreal",
        type = LocationType.CITY,
        country = "CA",
    )
    private val neighbourhood = LocationEntity(
        id = 333L,
        name = "Downtown",
        type = LocationType.NEIGHBORHOOD,
        parentId = city.id,
        country = "CA",
    )

    @BeforeEach
    fun setUp() {
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!, LocationType.NEIGHBORHOOD)
        doReturn(city).whenever(locationService).get(city.id!!, LocationType.CITY)
    }

    @Test
    fun `new CITY - generate content`() {
        // GIVEN
        doReturn(emptyList<PlaceEntity>()).whenever(placeService).search(
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

        val place = PlaceEntity(id = 777L, type = PlaceType.CITY)
        doReturn(place).whenever(placeService).create(any())

        // WHEN
        val result = worker.generate(city.id!!, LocationType.CITY)

        // THEN
        assertTrue(result)

        val request = argumentCaptor<CreatePlaceRequest>()
        verify(placeService).create(request.capture())
        assertEquals(city.id, request.firstValue.cityId)
        assertEquals(null, request.firstValue.neighbourhoodId)
        assertEquals(PlaceType.CITY, request.firstValue.type)
        assertEquals(city.name, request.firstValue.name)

        val event = argumentCaptor<PlaceCreatedEvent>()
        verify(placeCreatedEventHandler).handle(event.capture())
        assertEquals(place.id, event.firstValue.placeId)

        verify(placeUpdatePlaceEventHandler, never()).handle(any())
    }

    @Test
    fun `existing city, status=DRAFT, no content - generate the content`() {
        // GIVEN
        val place = PlaceEntity(id = 777L, type = PlaceType.CITY, status = PlaceStatus.DRAFT)
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

        // WHEN
        val result = worker.generate(city.id!!, LocationType.CITY)

        // THEN
        assertTrue(result)

        val event = argumentCaptor<PlaceUpdatedEvent>()
        verify(placeUpdatePlaceEventHandler).handle(event.capture())
        assertEquals(place.id, event.firstValue.placeId)

        verify(placeCreatedEventHandler, never()).handle(any())
    }

    @Test
    fun `existing city, status=PUBLISHING - do not generate content`() {
        // GIVEN
        val place = PlaceEntity(
            id = 777L,
            type = PlaceType.CITY,
            status = PlaceStatus.PUBLISHING,
        )
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

        // WHEN
        val result = worker.generate(city.id!!, LocationType.CITY)

        // THEN
        assertTrue(result)

        verify(placeUpdatePlaceEventHandler, never()).handle(any())
        verify(placeCreatedEventHandler, never()).handle(any())
    }

    @Test
    fun `new neighbourhood`() {
        // GIVEN
        doReturn(emptyList<PlaceEntity>()).whenever(placeService).search(
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

        val place = PlaceEntity(id = 777L, type = PlaceType.NEIGHBORHOOD)
        doReturn(place).whenever(placeService).create(any())

        // WHEN
        val result = worker.generate(neighbourhood.id!!, LocationType.NEIGHBORHOOD)

        // THEN
        assertTrue(result)

        val request = argumentCaptor<CreatePlaceRequest>()
        verify(placeService).create(request.capture())
        assertEquals(neighbourhood.parentId, request.firstValue.cityId)
        assertEquals(neighbourhood.id, request.firstValue.neighbourhoodId)
        assertEquals(PlaceType.NEIGHBORHOOD, request.firstValue.type)
        assertEquals(neighbourhood.name, request.firstValue.name)

        val event = argumentCaptor<PlaceCreatedEvent>()
        verify(placeCreatedEventHandler).handle(event.capture())
        assertEquals(place.id, event.firstValue.placeId)

        verify(placeUpdatePlaceEventHandler, never()).handle(any())
    }
}
