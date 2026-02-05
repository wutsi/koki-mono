package com.wutsi.koki.place.server.service.generator

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ai.NeighborhoodRatingResult
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.place.server.service.ai.RatingCriteraResult
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class NeighbourhoodContentGeneratorTest {
    private val factory = mock<PlaceAgentFactory>()
    private val jsonMapper = JsonMapper()
    private val persister = mock<NeighbourhoodPersister>()
    private val locationService = mock<LocationService>()
    private val generator = NeighbourhoodContentGenerator(
        factory,
        jsonMapper,
        persister,
        locationService,
    )

    private val agent = mock<Agent>()
    private val city = LocationEntity(id = 333L, parentId = 1L, name = "Yaounde")
    private val neighbourhood = LocationEntity(id = 111L, parentId = city.id, name = "Bastos")
    private val place = PlaceEntity(
        id = 777L,
        name = "Bastos",
        type = PlaceType.NEIGHBORHOOD,
        cityId = city.id!!,
        neighbourhoodId = neighbourhood.id!!,
        status = PlaceStatus.DRAFT
    )

    private val result = NeighbourhoodContentGeneratorResult(
        summary = "summary",
        introduction = "introduction",
        description = "description",
        summaryFr = "summaryFr",
        introductionFr = "introductionFr",
        descriptionFr = "descriptionFr",
        ratings = NeighborhoodRatingResult(
            security = RatingCriteraResult(4, "Good security"),
            education = RatingCriteraResult(3, "Average education"),
            infrastructure = RatingCriteraResult(4, "Great infrastructure"),
            commute = RatingCriteraResult(3, "Fair commute"),
            amenities = RatingCriteraResult(4, "Good amenities"),
        ),
    )

    @BeforeEach
    fun setUp() {
        doReturn(jsonMapper.writeValueAsString(result)).whenever(agent).run(any())
        doReturn(agent).whenever(factory).createNeighborhoodContentGeneratorAgent(neighbourhood, city)
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
        doReturn(city).whenever(locationService).get(city.id!!)
    }

    @Test
    fun generate() {
        // WHEN
        generator.generate(place)

        // THEN
        verify(agent).run("")
        verify(persister).persist(place, neighbourhood, result)
    }
}
