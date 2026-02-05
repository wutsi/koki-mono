package com.wutsi.koki.place.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ContentGeneratorAgentFactory
import com.wutsi.koki.place.server.service.PlaceContentGenerator
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class UpdatePlaceEventHandlerTest {
    private val placeService = mock<PlaceService>()
    private val contentGeneratorFactory = mock<ContentGeneratorAgentFactory>()
    private val locationService = mock<LocationService>()
    private val logger: KVLogger = DefaultKVLogger()
    private val handler = UpdatePlaceEventHandler(
        placeService = placeService,
        contentGeneratorFactory = contentGeneratorFactory,
        logger = logger,
    )

    private val generator = mock<PlaceContentGenerator>()
    private val neighbourhood = LocationEntity(id = 111L, parentId = 333L, name = "Bastos")
    private val city = LocationEntity(id = 333L, parentId = 1L, name = "Yaounde")

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @BeforeEach
    fun setUp() {
        doReturn(generator).whenever(contentGeneratorFactory).get(any())
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
        doReturn(city).whenever(locationService).get(city.id!!)
    }

    @Test
    fun `when place is PUBLISHING, do not generate content and return false`() {
        // GIVEN
        val place = PlaceEntity(id = 11L, status = PlaceStatus.PUBLISHING)
        doReturn(place).whenever(placeService).get(any())

        // WHEN
        val result = handler.handle(PlaceUpdatedEvent(11L))

        // THEN
        assertFalse(result)
        verify(generator, never()).generate(any())
    }

    @Test
    fun `when place is DRAFT, generate content and return true`() {
        // GIVEN
        val place = PlaceEntity(id = 11L, status = PlaceStatus.DRAFT)
        doReturn(place).whenever(placeService).get(any())

        // WHEN
        val result = handler.handle(PlaceUpdatedEvent(11L))

        // THEN
        assertTrue(result)
        verify(generator).generate(place)
    }

    @Test
    fun `when place is PUBLISHED, generate content and return true`() {
        // GIVEN
        val place = PlaceEntity(id = 11L, status = PlaceStatus.PUBLISHED)
        doReturn(place).whenever(placeService).get(any())

        // WHEN
        val result = handler.handle(PlaceUpdatedEvent(11L))

        // THEN
        assertTrue(result)
        verify(generator).generate(place)
    }
}
