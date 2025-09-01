package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.SearchListingResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.net.URLEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/SearchListingEndpoint.sql"])
class SearchListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity(
            "/v1/listings?limit=20",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(20, response.body!!.listings.size)
        assertEquals(27, response.body!!.total)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity(
            "/v1/listings?id=100&id=101&sort-by=PRICE_LOW_HIGH",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(listOf(100L, 101L), listings.map { listing -> listing.id }.sorted())
    }

    @Test
    fun `by listing-number`() {
        val response = rest.getForEntity(
            "/v1/listings?listing-number=1000000",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(100L, listings[0].id)
    }

    @Test
    fun `by location`() {
        val response = rest.getForEntity(
            "/v1/listings?location-id=2222&location-id=3333&sort-by=PRICE_HIGH_LOW",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(102L, 103L)))
    }

    @Test
    fun `by listing-type`() {
        val response = rest.getForEntity(
            "/v1/listings?listing-type=RENTAL&sort-by=NEWEST",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(104, listings[0].id)
    }

    @Test
    fun `by property-type`() {
        val response = rest.getForEntity(
            "/v1/listings?property-type=STUDIO&sort-by=OLDEST",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(105, listings[0].id)
    }

    @Test
    fun `by furniture-type`() {
        val response = rest.getForEntity(
            "/v1/listings?furniture-type=SEMI_FURNISHED",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(106, listings[0].id)
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity(
            "/v1/listings?status=SOLD",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(107, listings[0].id)
    }

    @Test
    fun `by bedroom`() {
        val response = rest.getForEntity(
            "/v1/listings?bedrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(108, listings[0].id)
    }

    @Test
    fun `by bedroom+`() {
        val response = rest.getForEntity(
            "/v1/listings?bedrooms=" + URLEncoder.encode("2+", "utf-8"),
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(108L, 109L)))
    }

    @Test
    fun `by bathroom`() {
        val response = rest.getForEntity(
            "/v1/listings?bathrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(111, listings[0].id)
    }

    @Test
    fun `by bathroom+`() {
        val response = rest.getForEntity(
            "/v1/listings?bathrooms=" + URLEncoder.encode("2+", "utf-8"),
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(111L, 112L)))
    }

    @Test
    fun `by price`() {
        val response = rest.getForEntity(
            "/v1/listings?min-price=500&max-price=1900",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(114L, 115L)))
    }

    @Test
    fun `by lot-area`() {
        val response = rest.getForEntity(
            "/v1/listings?min-lot-area=500&max-lot-area=1900",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(117L, 118L)))
    }

    @Test
    fun `by property-area`() {
        val response = rest.getForEntity(
            "/v1/listings?min-property-area=500&max-property-area=1900",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(120L, 121L)))
    }

    @Test
    fun `by seller agent`() {
        val response = rest.getForEntity(
            "/v1/listings?seller-agent-user-id=1234",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(123, listings[0].id)
    }

    @Test
    fun `by buyer agent`() {
        val response = rest.getForEntity(
            "/v1/listings?buyer-agent-user-id=4567&sort-by=TRANSACTION_DATE",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(124, listings[0].id)
    }

    @Test
    fun `by agent`() {
        val response = rest.getForEntity(
            "/v1/listings?agent-user-id=1111",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(125L, 126L)))
    }
}
