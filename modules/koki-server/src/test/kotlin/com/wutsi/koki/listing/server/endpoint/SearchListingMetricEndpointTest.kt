package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.dto.SearchListingMetricResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/SearchListingMetricEndpoint.sql"])
class SearchListingMetricEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity(
            "/v1/listings/metrics",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(5, metrics.size)
    }

    @Test
    fun `by neighbourhood-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.neighborhoodId == 1000L })
    }

    @Test
    fun `by agent-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L })
    }

    @Test
    fun `by multiple agent-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100&seller-agent-user-id=101",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(3, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L || it.sellerAgentUserId == 101L })
    }

    @Test
    fun `by city-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=10",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(3, metrics.size)
        assertTrue(metrics.all { it.cityId == 10L })
    }

    @Test
    fun `by bedrooms`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?bedrooms=3",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.bedrooms == 3 })
    }

    @Test
    fun `by property-category`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?property-category=RESIDENTIAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(3, metrics.size) // IDs 100, 101, 102 all have RESIDENTIAL (propertyCategory=0)
        assertTrue(metrics.all { it.propertyCategory == PropertyCategory.RESIDENTIAL })
    }

    @Test
    fun `by listing-type SALE`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size)
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `by seller-agent-user-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=101",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(101L, metrics[0].sellerAgentUserId)
        assertEquals(1001L, metrics[0].neighborhoodId)
    }

    @Test
    fun `verify metric data structure`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)

        val metric = metrics[0]
        assertEquals(1000L, metric.neighborhoodId)
        assertEquals(10L, metric.total)
        assertEquals(100000L, metric.minPrice)
        assertEquals(500000L, metric.maxPrice)
        assertEquals(300000L, metric.averagePrice)
        assertEquals(500, metric.averageLotArea)
        assertEquals(600, metric.pricePerSquareMeter)
        assertEquals(3000000L, metric.totalPrice)
        assertEquals("XAF", metric.currency)
    }

    @Test
    fun `dimension NEIGHBORHOOD`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=NEIGHBORHOOD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size) // 4 distinct neighborhoods for SALE type

        // Each metric should have a distinct neighborhood
        val neighborhoods = metrics.mapNotNull { it.neighborhoodId }.sorted()
        assertEquals(listOf(1000L, 1001L, 1002L, 1003L), neighborhoods)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension CITY`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=CITY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // 2 distinct cities for SALE type (10, 11)

        // Each metric should have a distinct city
        val cities = metrics.mapNotNull { it.cityId }.sorted()
        assertEquals(listOf(10L, 11L), cities)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension SELLER_AGENT`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=SELLER_AGENT",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size) // 4 distinct agents for SALE type

        // Each metric should have a distinct seller agent
        val agents = metrics.mapNotNull { it.sellerAgentUserId }.sorted()
        assertEquals(listOf(100L, 101L, 102L, 103L), agents)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension BEDROOMS`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=BEDROOMS",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(3, metrics.size) // 2 distinct bedroom counts for SALE type (3, 4)

        // Each metric should have a distinct bedroom count
        val bedrooms = metrics.mapNotNull { it.bedrooms }.sorted()
        assertEquals(listOf(3, 4), bedrooms)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension PROPERTY_CATEGORY`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=PROPERTY_CATEGORY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(3, metrics.size) // 3 distinct property categories for SALE type

        // Each metric should have a distinct property category
        val categories = metrics.map { it.propertyCategory }.sortedBy { it.ordinal }
        assertEquals(
            listOf(PropertyCategory.RESIDENTIAL, PropertyCategory.LAND, PropertyCategory.COMMERCIAL),
            categories
        )

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }
}
