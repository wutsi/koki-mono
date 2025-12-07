package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.SearchSimilarListingResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/SearchSimilarListingEndpoint.sql"])
class SearchSimilarListingEndpointTest : AuthorizationAwareEndpointTest() {

    @Test
    fun `search similar listings`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?limit=5",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertTrue(listings.isNotEmpty())

        // Verify scores are sorted in descending order
        val scores = listings.map { it.score }
        assertEquals(scores.sortedDescending(), scores)

        // Verify all scores are between 0 and 1
        assertTrue(scores.all { it in 0.0..1.0 })

        // Verify reference listing is not in results
        assertTrue(listings.none { it.id == 100L })
    }

    @Test
    fun `search similar with status filter`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?status=SOLD&limit=10",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertTrue(listings.isNotEmpty())
    }

    @Test
    fun `search similar with same agent filter`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?same-agent=true&limit=10",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        // Results should only contain listings with the same agent
    }

    @Test
    fun `search similar with same city filter`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?same-city=true&limit=10",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertTrue(listings.isNotEmpty())
    }

    @Test
    fun `search similar with same neighborhood filter`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?same-neighborhood=true&limit=10",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `limit is capped at 50`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar?limit=100",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertTrue(listings.size <= 50)
    }

    @Test
    fun `default limit is 10`() {
        val response = rest.getForEntity(
            "/v1/listings/100/similar",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertTrue(listings.size <= 10)
    }

    @Test
    fun `listing not found returns 404`() {
        val response = rest.getForEntity(
            "/v1/listings/99999/similar",
            SearchSimilarListingResponse::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
