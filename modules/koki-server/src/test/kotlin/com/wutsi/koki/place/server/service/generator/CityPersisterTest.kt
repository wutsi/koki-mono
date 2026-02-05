package com.wutsi.koki.place.server.service.generator

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ai.CityContentGeneratorResult
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class CityPersisterTest {
    private val dao = mock<PlaceRepository>()
    private val persister = CityPersister(dao)

    private val result = CityContentGeneratorResult(
        summary = "summary",
        introduction = "introduction",
        description = "description",
        summaryFr = "summaryFr",
        introductionFr = "introductionFr",
        descriptionFr = "descriptionFr",
    )

    @Test
    fun persist() {
        // GIVEN
        val place = PlaceEntity(id = 111L)
        val city = LocationEntity(latitude = 1.0, longitude = 2.0)

        // WHEN
        persister.persist(place, city, result)

        // THEN
        assertEquals(result.summary, place.summary)
        assertEquals(result.introduction, place.introduction)
        assertEquals(result.description, place.description)
        assertEquals(result.summaryFr, place.summaryFr)
        assertEquals(result.introductionFr, place.introductionFr)
        assertEquals(result.descriptionFr, place.descriptionFr)
        assertEquals(city.latitude, place.latitude)
        assertEquals(city.longitude, place.longitude)
        assertEquals(null, place.rating)
        verify(dao).save(place)
    }
}
