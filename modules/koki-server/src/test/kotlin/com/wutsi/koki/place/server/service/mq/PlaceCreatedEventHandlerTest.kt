package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ContentGeneratorAgentFactory
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.place.server.service.PlaceService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertFalse

class PlaceCreatedEventHandlerTest {
    private val placeService = mock<PlaceService>()
    private val contentGeneratorFactory = mock<ContentGeneratorAgentFactory>()
    private val handler = PlaceCreatedEventHandler(
        placeService = placeService,
        contentGeneratorFactory = contentGeneratorFactory,
    )

    private val generator = mock<PlaceContentGenerator>()
    private val place = PlaceEntity(
        id = 777L,
        name = "Bastos",
        type = PlaceType.NEIGHBORHOOD,
        cityId = 111L,
        neighbourhoodId = 333L,
        status = PlaceStatus.DRAFT
    )

    @BeforeEach
    fun setUp() {
        doReturn(generator).whenever(contentGeneratorFactory).get(any())
        doReturn(place).whenever(placeService).get(any())
    }

    @Test
    fun handle() {
        // WHEN
        val result = handler.handle(PlaceCreatedEvent(placeId = place.id!!))

        // THEN
        assertTrue(result)

        verify(generator).generate(place)

        verify(placeService, times(2)).save(any())
        assertEquals(PlaceStatus.PUBLISHED, place.status)
    }

    @Test
    fun `do not handle when place status is not PUBLISHING`() {
        // GIVEN
        doReturn(place.copy(status = PlaceStatus.PUBLISHING)).whenever(placeService).get(any())

        // WHEN
        val result = handler.handle(PlaceCreatedEvent(placeId = place.id!!))

        // THEN
        assertFalse(result)

        verify(generator, never()).generate(place)

        verify(placeService, never()).save(any())
    }

    @Test
    fun `do not handle when place status is not PUBLISHED`() {
        // GIVEN
        doReturn(place.copy(status = PlaceStatus.PUBLISHED)).whenever(placeService).get(any())

        // WHEN
        val result = handler.handle(PlaceCreatedEvent(placeId = place.id!!))

        // THEN
        assertFalse(result)

        verify(generator, never()).generate(place)

        verify(placeService, never()).save(any())
    }
}
