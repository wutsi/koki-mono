package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.SearchListingResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
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
        assertEquals(30, response.body!!.total)
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

    // =====================================================
    // Dimension Searches - Bedrooms
    // =====================================================

    @Test
    fun `by bedroom exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-bedrooms=2&max-bedrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(108, listings[0].id)
    }

    @Test
    fun `by bedroom minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-bedrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(108L, 109L)))
    }

    @Test
    fun `by bedroom maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-bedrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(108L, 110L)))
    }

    // =====================================================
    // Dimension Searches - Bathrooms
    // =====================================================

    @Test
    fun `by bathroom exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-bathrooms=2&max-bathrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(111, listings[0].id)
    }

    @Test
    fun `by bathroom minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-bathrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(111L, 112L)))
    }

    @Test
    fun `by bathroom maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-bathrooms=2",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(111L, 113L)))
    }

    // =====================================================
    // Dimension Searches - Price
    // =====================================================

    @Test
    fun `by price range`() {
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
    fun `by price minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-price=1500",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(115L, 116L)))
    }

    @Test
    fun `by price maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-price=1500",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(114L, 115L)))
    }

    @Test
    fun `by price exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-price=1500&max-price=1500",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(115L, listings[0].id)
    }

    // =====================================================
    // Dimension Searches - Lot Area
    // =====================================================

    @Test
    fun `by lot-area range`() {
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
    fun `by lot-area minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-lot-area=1200",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(118L, 119L)))
    }

    @Test
    fun `by lot-area maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-lot-area=1200",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(117L, 118L)))
    }

    @Test
    fun `by lot-area exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-lot-area=1200&max-lot-area=1200",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(118L, listings[0].id)
    }

    // =====================================================
    // Dimension Searches - Property Area
    // =====================================================

    @Test
    fun `by property-area range`() {
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
    fun `by property-area minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-property-area=1100",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(121L, 122L)))
    }

    @Test
    fun `by property-area maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-property-area=1100",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(120L, 121L)))
    }

    @Test
    fun `by property-area exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-property-area=1100&max-property-area=1100",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(121L, listings[0].id)
    }

    // =====================================================
    // Agent Searches
    // =====================================================

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

    // =====================================================
    // Dimension Searches - Sale Price
    // =====================================================

    @Test
    fun `by sale-price range`() {
        val response = rest.getForEntity(
            "/v1/listings?min-sale-price=250000&max-sale-price=400000",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(127L, 128L)))
    }

    @Test
    fun `by sale-price minimum`() {
        val response = rest.getForEntity(
            "/v1/listings?min-sale-price=350000",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(128L, 129L)))
    }

    @Test
    fun `by sale-price maximum`() {
        val response = rest.getForEntity(
            "/v1/listings?max-sale-price=350000",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(2, listings.size)
        assertEquals(true, listings.map { listing -> listing.id }.containsAll(listOf(127L, 128L)))
    }

    @Test
    fun `by sale-price exact match`() {
        val response = rest.getForEntity(
            "/v1/listings?min-sale-price=350000&max-sale-price=350000",
            SearchListingResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val listings = response.body!!.listings
        assertEquals(1, listings.size)
        assertEquals(128L, listings[0].id)
    }
}
