package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.CreatePlaceResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.dao.PlaceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/place/CreatePlaceEndpoint.sql"])
class CreatePlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    private val request = CreatePlaceRequest(
        name = "New School",
        type = PlaceType.SCHOOL,
        neighbourhoodId = 111L,
    )

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity("/v1/places", request, CreatePlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val placeId = response.body!!.placeId
        val place = dao.findById(placeId).get()

        // Basic fields
        assertEquals(request.name, place.name)
        assertEquals(request.type, place.type)
        assertEquals(request.neighbourhoodId, place.neighbourhoodId)
        assertEquals(PlaceStatus.DRAFT, place.status)
        assertEquals(TENANT_ID, place.tenantId)

        // Audit fields
        assertEquals(USER_ID, place.createdById)
        assertEquals(USER_ID, place.modifiedById)
        assertNull(place.deletedAt)

        // AI-generated content should be populated
        assertNotNull(place.summary)
        assertNotNull(place.summaryFr)
        assertNotNull(place.introduction)
        assertNotNull(place.introductionFr)
        assertNotNull(place.description)
        assertNotNull(place.descriptionFr)
    }

    @Test
    fun `create neighborhood`() {
        // GIVEN
        val neighborhoodRequest = CreatePlaceRequest(
            name = "Downtown",
            type = PlaceType.NEIGHBORHOOD,
            neighbourhoodId = 111L,
        )

        // WHEN
        val response = rest.postForEntity("/v1/places", neighborhoodRequest, CreatePlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val placeId = response.body!!.placeId
        val place = dao.findById(placeId).get()
        assertEquals(PlaceType.NEIGHBORHOOD, place.type)
        assertNotNull(place.summary)
    }

    @Test
    fun `create park`() {
        // GIVEN
        val parkRequest = CreatePlaceRequest(
            name = "Central Park",
            type = PlaceType.PARK,
            neighbourhoodId = 111L,
        )

        // WHEN
        val response = rest.postForEntity("/v1/places", parkRequest, CreatePlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val placeId = response.body!!.placeId
        val place = dao.findById(placeId).get()
        assertEquals(PlaceType.PARK, place.type)
        assertNotNull(place.summary)
    }
}
