package com.wutsi.koki.place.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.service.ai.NeighborhoodRatingResult
import com.wutsi.koki.place.server.service.ai.NeighbourhoodContentGeneratorResult
import com.wutsi.koki.place.server.service.ai.PlaceAgentFactory
import com.wutsi.koki.place.server.service.ai.RatingCriteraResult
import com.wutsi.koki.platform.ai.agent.Agent
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import tools.jackson.databind.json.JsonMapper
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/place/UpdatePlaceEndpoint.sql"])
class UpdatePlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    @Autowired
    private lateinit var ratingDao: PlaceRatingRepository

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var placeAgentFactory: PlaceAgentFactory

    private val agent = mock<Agent>()

    @Test
    fun neighborhood() {
        // GIVEN
        val result = NeighbourhoodContentGeneratorResult(
            summary = "Some summary",
            summaryFr = "Résumé",
            introduction = "Some introduction",
            introductionFr = "Introduction",
            description = "Some description",
            descriptionFr = "Description",
            ratings = NeighborhoodRatingResult(
                security = RatingCriteraResult(4, "Safe area"),
                amenities = RatingCriteraResult(5, "Many amenities"),
                infrastructure = RatingCriteraResult(3, "Good infrastructure"),
                lifestyle = RatingCriteraResult(4, "Vibrant lifestyle"),
                commute = RatingCriteraResult(2, "Average commute"),
                education = RatingCriteraResult(3, "Average commute"),
            ),
        )
        val json = jsonMapper.writeValueAsString(result)
        doReturn(json).whenever(agent).run(any())

        doReturn(agent).whenever(placeAgentFactory).createNeighborhoodContentGeneratorAgent(any(), any())

        // WHEN
        val response = rest.postForEntity("/v1/places/100", emptyMap<String, String>(), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val placeId = 100L
        val place = dao.findById(placeId).get()

        assertEquals(PlaceType.NEIGHBORHOOD, place.type)
        assertEquals(222L, place.neighbourhoodId)
        assertEquals(PlaceStatus.DRAFT, place.status)
        assertEquals(TENANT_ID, place.tenantId)
        assertEquals("Côte-des-Neiges", place.name)
        assertEquals(result.introduction, place.introduction)
        assertEquals(result.summary, place.summary)
        assertEquals(result.description, place.description)
        assertEquals("cote-des-neiges", place.asciiName)
        assertEquals(result.introductionFr, place.introductionFr)
        assertEquals(result.summaryFr, place.summaryFr)
        assertEquals(result.descriptionFr, place.descriptionFr)
        assertEquals(3.5, place.rating)
        assertEquals(45.4972159, place.latitude)
        assertEquals(-73.6390246, place.longitude)
        assertEquals(USER_ID, place.createdById)
        assertEquals(USER_ID, place.modifiedById)
        assertFalse(place.deleted)
        assertNull(place.deletedAt)

        assertRating(placeId, RatingCriteria.SECURITY, result.ratings.security.value)
        assertRating(placeId, RatingCriteria.INFRASTRUCTURE, result.ratings.infrastructure.value)
        assertRating(placeId, RatingCriteria.AMENITIES, result.ratings.amenities.value)
        assertRating(placeId, RatingCriteria.LIFESTYLE, result.ratings.lifestyle.value)
        assertRating(placeId, RatingCriteria.COMMUTE, result.ratings.commute.value)
    }

    private fun assertRating(placeId: Long, criteria: RatingCriteria, expected: Int) {
        val rating = ratingDao.findByPlaceIdAndCriteria(placeId, criteria)
        assertEquals(expected, rating?.value)
    }
}
