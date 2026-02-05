package com.wutsi.koki.place.server.service.generator

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ai.CityContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class CityContentGeneratorTest {
    private val factory = mock<PlaceAgentFactory>()
    private val jsonMapper = JsonMapper()
    private val persister = mock<CityPersister>()
    private val locationService = mock<LocationService>()
    private val generator = CityContentGenerator(
        factory,
        jsonMapper,
        persister,
        locationService,
    )

    private val agent = mock<Agent>()
    private val city = LocationEntity(id = 333L, parentId = 1L, name = "Yaounde")
    private val place = PlaceEntity(
        id = 777L,
        name = "Bastos",
        type = PlaceType.CITY,
        cityId = city.id!!,
        status = PlaceStatus.DRAFT
    )

    private val result = CityContentGeneratorResult(
        summary = "summary",
        introduction = "introduction",
        description = "description",
        summaryFr = "summaryFr",
        introductionFr = "introductionFr",
        descriptionFr = "descriptionFr",
    )

    @BeforeEach
    fun setUp() {
        doReturn(jsonMapper.writeValueAsString(result)).whenever(agent).run(any())
        doReturn(agent).whenever(factory).createCityContentGeneratorAgent(city)
        doReturn(city).whenever(locationService).get(city.id!!)
    }

    @Test
    fun generate() {
        // WHEN
        generator.generate(place)

        // THEN
        verify(agent).run("")
        verify(persister).persist(place, city, result)
    }
}
