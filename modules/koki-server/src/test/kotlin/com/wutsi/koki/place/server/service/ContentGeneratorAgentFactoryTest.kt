package com.wutsi.koki.place.server.service

import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.service.generator.CityContentGenerator
import com.wutsi.koki.place.server.service.generator.NeighbourhoodContentGenerator
import com.wutsi.koki.place.server.service.generator.NullContentGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class ContentGeneratorAgentFactoryTest {
    private val neighbourhood = mock<NeighbourhoodContentGenerator>()
    private val city = mock<CityContentGenerator>()
    private val factory = ContentGeneratorAgentFactory(neighbourhood, city)

    @Test
    fun `place - city`() {
        val generator = factory.get(PlaceType.CITY)
        assertTrue(generator is CityContentGenerator)
    }

    @Test
    fun `place - neighbourhood`() {
        val generator = factory.get(PlaceType.NEIGHBORHOOD)
        assertTrue(generator is NeighbourhoodContentGenerator)
    }

    @Test
    fun `place - hospital`() {
        val generator = factory.get(PlaceType.HOSPITAL)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - museup`() {
        val generator = factory.get(PlaceType.MUSEUM)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - supermarket`() {
        val generator = factory.get(PlaceType.SUPERMARKET)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - market`() {
        val generator = factory.get(PlaceType.MARKET)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - school`() {
        val generator = factory.get(PlaceType.SCHOOL)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - park`() {
        val generator = factory.get(PlaceType.PARK)
        assertTrue(generator is NullContentGenerator)
    }

    @Test
    fun `place - unknown`() {
        val generator = factory.get(PlaceType.UNKNOWN)
        assertTrue(generator is NullContentGenerator)
    }
}
