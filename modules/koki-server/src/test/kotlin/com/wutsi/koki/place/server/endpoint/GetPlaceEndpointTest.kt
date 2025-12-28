package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.place.dto.GetPlaceResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/place/GetPlaceEndpoint.sql"])
class GetPlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        // WHEN
        val response = rest.getForEntity("/v1/places/100", GetPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val place = response.body!!.place
        assertEquals(100L, place.id)
        assertEquals(PlaceType.SCHOOL, place.type)
        assertEquals(PlaceStatus.DRAFT, place.status)
        assertEquals(111L, place.neighbourhoodId)
        assertEquals("Downtown Park", place.name)
        assertEquals("A beautiful park", place.summary)
        assertEquals(4.5, place.rating)

        // Should include ratings
        assertEquals(2, place.ratingCriteria.size)

        // Timestamps
        assertNotNull(place.createdAt)
        assertNotNull(place.modifiedAt)
    }

    @Test
    fun `get not found`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/999", ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PLACE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `get from different tenant`() {
        // WHEN - Try to get place from tenant 2
        val response = rest.getForEntity("/v1/places/200", ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PLACE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `get deleted place`() {
        // WHEN - Try to get deleted place
        val response = rest.getForEntity("/v1/places/300", ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.PLACE_NOT_FOUND, response.body?.error?.code)
    }
}
