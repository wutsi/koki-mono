package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SearchPlaceResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/SearchPlaceEndpoint.sql"])
class SearchPlaceEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `search all places`() {
        // WHEN
        val response = rest.getForEntity("/v1/places", SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        // Should return 5 places from tenant 1 (excluding deleted 300 and tenant 2's 200)
        assertEquals(6, places.size)
    }

    @Test
    fun `search by neighbourhood`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?neighbourhood-id=111",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(4, places.size) // 100, 101, 103, 104
        assertTrue(places.all { it.neighbourhoodId == 111L })
    }

    @Test
    fun `search by multiple neighbourhoods`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?neighbourhood-id=111&neighbourhood-id=222",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(5, places.size) // All non-deleted places
    }

    @Test
    fun `search by city`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?city-id=2",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(1, places.size) // 200
    }

    @Test
    fun `search by type`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?type=SCHOOL",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(2, places.size) // 101, 103
        assertTrue(places.all { it.type == PlaceType.SCHOOL })
    }

    @Test
    fun `search by multiple types`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?type=SCHOOL&type=PARK",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(4, places.size) // 100, 101, 102, 103
    }

    @Test
    fun `search by status`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?status=PUBLISHED",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(4, places.size) // 100, 101, 102, 104
        assertTrue(places.all { it.type != PlaceType.UNKNOWN })
    }

    @Test
    fun `search by keyword`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?q=EcoLE",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(2, places.size) // 101, 103 (both have "School" in name)
    }

    @Test
    fun `search with combined filters`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/places?neighbourhood-id=111&type=SCHOOL&status=PUBLISHED",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(1, places.size) // Only 101 matches all criteria
        assertEquals(101L, places[0].id)
    }

    @Test
    fun `search with pagination`() {
        // WHEN - Get first 2 results
        val response1 = rest.getForEntity(
            "/v1/places?limit=2&offset=0",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response1.statusCode)
        assertEquals(2, response1.body!!.places.size)

        // WHEN - Get next 2 results
        val response2 = rest.getForEntity(
            "/v1/places?limit=2&offset=2",
            SearchPlaceResponse::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response2.statusCode)
        assertEquals(2, response2.body!!.places.size)

        // Results should be different
        val ids1 = response1.body!!.places.map { it.id }
        val ids2 = response2.body!!.places.map { it.id }
        assertTrue(ids1.intersect(ids2.toSet()).isEmpty())
    }

    @Test
    fun `search excludes deleted places`() {
        // WHEN
        val response = rest.getForEntity("/v1/places", SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        // Should not include deleted place (300)
        assertTrue(places.none { it.id == 300L })
    }

    @Test
    fun `search excludes other tenant places`() {
        // WHEN
        val response = rest.getForEntity("/v1/places", SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        // Should not include tenant 2 place (200)
        assertTrue(places.none { it.id == 200L })
    }
}
