package com.wutsi.koki.place.server.service.generator

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.domain.PlaceRatingEntity
import com.wutsi.koki.place.server.service.ai.NeighborhoodRatingResult
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.RatingCriteraResult
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class NeighbourhoodPersisterTest {
    private val dao = mock<PlaceRepository>()
    private val ratingDao = mock<PlaceRatingRepository>()
    private val persister = NeighbourhoodPersister(dao, ratingDao)

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

    @Test
    fun persist() {
        // GIVEN
        val place = PlaceEntity(id = 111L)
        val neighbourhood = LocationEntity(latitude = 1.0, longitude = 2.0)

        val securityRating = PlaceRatingEntity(
            id = 333L,
            placeId = place.id!!,
            criteria = RatingCriteria.SECURITY,
            value = -1,
            reason = "",
        )
        doReturn(securityRating).doReturn(null)
            .doReturn(null)
            .doReturn(null)
            .doReturn(null)
            .whenever(ratingDao).findByPlaceIdAndCriteria(any(), any())

        // WHEN
        persister.persist(place, neighbourhood, result)

        // THEN
        assertEquals(result.summary, place.summary)
        assertEquals(result.introduction, place.introduction)
        assertEquals(result.description, place.description)
        assertEquals(result.summaryFr, place.summaryFr)
        assertEquals(result.introductionFr, place.introductionFr)
        assertEquals(result.descriptionFr, place.descriptionFr)
        assertEquals(neighbourhood.latitude, place.latitude)
        assertEquals(neighbourhood.longitude, place.longitude)
        assertEquals(3.6, place.rating)
        verify(dao).save(place)

        val rating = argumentCaptor<PlaceRatingEntity>()
        verify(ratingDao, times(5)).save(rating.capture())

        assertEquals(securityRating.id, rating.firstValue.id)
        assertEquals(securityRating.placeId, rating.firstValue.placeId)
        assertEquals(result.ratings.security.value, rating.firstValue.value)
        assertEquals(result.ratings.security.reason, rating.firstValue.reason)
        assertEquals(RatingCriteria.SECURITY, rating.firstValue.criteria)

        assertEquals(null, rating.secondValue.id)
        assertEquals(place.id, rating.secondValue.placeId)
        assertEquals(result.ratings.education.value, rating.secondValue.value)
        assertEquals(result.ratings.education.reason, rating.secondValue.reason)
        assertEquals(RatingCriteria.EDUCATION, rating.secondValue.criteria)

        assertEquals(null, rating.thirdValue.id)
        assertEquals(place.id, rating.thirdValue.placeId)
        assertEquals(result.ratings.infrastructure.value, rating.thirdValue.value)
        assertEquals(result.ratings.infrastructure.reason, rating.thirdValue.reason)
        assertEquals(RatingCriteria.INFRASTRUCTURE, rating.thirdValue.criteria)

        assertEquals(null, rating.allValues[3].id)
        assertEquals(place.id, rating.allValues[3].placeId)
        assertEquals(result.ratings.commute.value, rating.allValues[3].value)
        assertEquals(result.ratings.commute.reason, rating.allValues[3].reason)
        assertEquals(RatingCriteria.COMMUTE, rating.allValues[3].criteria)

        assertEquals(null, rating.allValues[4].id)
        assertEquals(place.id, rating.allValues[4].placeId)
        assertEquals(result.ratings.amenities.value, rating.allValues[4].value)
        assertEquals(result.ratings.amenities.reason, rating.allValues[4].reason)
        assertEquals(RatingCriteria.AMENITIES, rating.allValues[4].criteria)
    }
}
