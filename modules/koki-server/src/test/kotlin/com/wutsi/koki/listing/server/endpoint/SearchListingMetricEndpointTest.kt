package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.ListingStatus
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
    fun `by neighbourhood-id - no results`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=9999",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(0, metrics.size)
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
    fun `by agent-id - no results`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=9999",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(0, metrics.size)
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
    fun `by city-id - no results`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=9999",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(0, metrics.size)
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
    fun `by bedrooms - no results`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?bedrooms=10",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(0, metrics.size)
    }

    @Test
    fun `by bedrooms and property-category`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?bedrooms=3&property-category=RESIDENTIAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.bedrooms == 3 && it.propertyCategory == PropertyCategory.RESIDENTIAL })
    }

    @Test
    fun `by bedrooms and city-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?bedrooms=3&city-id=10",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.bedrooms == 3 && it.cityId == 10L })
    }

    @Test
    fun `by property-category RESIDENTIAL`() {
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
    fun `by property-category LAND`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?property-category=LAND",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(PropertyCategory.LAND, metrics[0].propertyCategory)
        assertEquals(1002L, metrics[0].neighborhoodId)
    }

    @Test
    fun `by property-category COMMERCIAL`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?property-category=COMMERCIAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(PropertyCategory.COMMERCIAL, metrics[0].propertyCategory)
        assertEquals(1003L, metrics[0].neighborhoodId)
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
    fun `by listing-type RENTAL`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=RENTAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(ListingType.RENTAL, metrics[0].listingType)
        assertEquals(1000L, metrics[0].neighborhoodId)
    }

    @Test
    fun `by listing-status SOLD`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-status=SOLD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(5, metrics.size)
        assertTrue(metrics.all { it.listingStatus == ListingStatus.SOLD })
    }

    @Test
    fun `by neighbourhood and property-category`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000&property-category=RESIDENTIAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.neighborhoodId == 1000L && it.propertyCategory == PropertyCategory.RESIDENTIAL })
    }

    @Test
    fun `by neighbourhood and listing-type`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(1000L, metrics[0].neighborhoodId)
        assertEquals(ListingType.SALE, metrics[0].listingType)
    }

    @Test
    fun `by property-category and listing-type`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?property-category=RESIDENTIAL&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // IDs 100 and 102 have RESIDENTIAL + SALE
        assertTrue(metrics.all { it.propertyCategory == PropertyCategory.RESIDENTIAL && it.listingType == ListingType.SALE })
    }

    @Test
    fun `by agent-id and property-category`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100&property-category=RESIDENTIAL",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L && it.propertyCategory == PropertyCategory.RESIDENTIAL })
    }

    @Test
    fun `by seller-agent-user-id returns correct agent`() {
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
    fun `by seller-agent-user-id and listing-type`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L && it.listingType == ListingType.SALE })
    }

    @Test
    fun `by seller-agent-user-id and listing-status`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=102&listing-status=SOLD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(102L, metrics[0].sellerAgentUserId)
        assertEquals(ListingStatus.SOLD, metrics[0].listingStatus)
    }

    @Test
    fun `by seller-agent-user-id and city-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100&city-id=10",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L && it.cityId == 10L })
    }

    @Test
    fun `by seller-agent-user-id and neighbourhood-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?seller-agent-user-id=100&neighbourhood-id=1000",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.sellerAgentUserId == 100L && it.neighborhoodId == 1000L })
    }

    @Test
    fun `verify different agents have different metrics`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        val agentIds = metrics.mapNotNull { it.sellerAgentUserId }.toSet()
        assertTrue(agentIds.size >= 3) // At least 3 different agents (100, 101, 102, 103)
        assertTrue(agentIds.contains(100L))
        assertTrue(agentIds.contains(101L))
        assertTrue(agentIds.contains(102L))
    }

    @Test
    fun `by city-id and listing-type`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=10&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size)
        assertTrue(metrics.all { it.cityId == 10L && it.listingType == ListingType.SALE })
    }

    @Test
    fun `with multiple filters`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000&property-category=RESIDENTIAL&listing-type=SALE&listing-status=SOLD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)
        assertEquals(1000L, metrics[0].neighborhoodId)
        assertEquals(PropertyCategory.RESIDENTIAL, metrics[0].propertyCategory)
        assertEquals(ListingType.SALE, metrics[0].listingType)
        assertEquals(ListingStatus.SOLD, metrics[0].listingStatus)
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
    fun `verify all neighbourhoods are returned`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size)

        val neighbourhoodIds = metrics.mapNotNull { it.neighborhoodId }.sorted()
        assertEquals(listOf(1000L, 1001L, 1002L, 1003L), neighbourhoodIds)
    }

    @Test
    fun `no filters returns all metrics`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(5, metrics.size)

        // Verify we have different property categories
        val categories = metrics.mapNotNull { it.propertyCategory }.toSet()
        assertEquals(3, categories.size) // RESIDENTIAL, LAND, COMMERCIAL
        assertTrue(categories.contains(PropertyCategory.RESIDENTIAL))
        assertTrue(categories.contains(PropertyCategory.LAND))
        assertTrue(categories.contains(PropertyCategory.COMMERCIAL))
    }

    @Test
    fun `dimension NEIGHBORHOOD groups by neighborhood`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=NEIGHBORHOOD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size) // 4 distinct neighborhoods for SALE type

        // Each metric should have a distinct neighborhood
        val neighborhoods = metrics.map { it.neighborhoodId }.filterNotNull().sorted()
        assertEquals(listOf(1000L, 1001L, 1002L, 1003L), neighborhoods)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension CITY groups by city`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=CITY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // 2 distinct cities for SALE type (10, 11)

        // Each metric should have a distinct city
        val cities = metrics.map { it.cityId }.filterNotNull().sorted()
        assertEquals(listOf(10L, 11L), cities)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension SELLER_AGENT groups by seller agent`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=SELLER_AGENT",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size) // 4 distinct agents for SALE type

        // Each metric should have a distinct seller agent
        val agents = metrics.map { it.sellerAgentUserId }.filterNotNull().sorted()
        assertEquals(listOf(100L, 101L, 102L, 103L), agents)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension BEDROOMS groups by bedrooms`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=BEDROOMS",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // 2 distinct bedroom counts for SALE type (3, 4)

        // Each metric should have a distinct bedroom count
        val bedrooms = metrics.map { it.bedrooms }.filterNotNull().sorted()
        assertEquals(listOf(3, 4), bedrooms)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension PROPERTY_CATEGORY groups by property category`() {
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

    @Test
    fun `dimension NEIGHBORHOOD with city filter`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=10&listing-type=SALE&dimension=NEIGHBORHOOD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // 2 neighborhoods in city 10 with SALE type

        // Each metric should have a distinct neighborhood
        val neighborhoods = metrics.map { it.neighborhoodId }.filterNotNull().sorted()
        assertEquals(listOf(1000L, 1001L), neighborhoods)

        // All should be SALE type
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
    }

    @Test
    fun `dimension CITY with property category filter`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?property-category=RESIDENTIAL&listing-type=SALE&dimension=CITY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size) // Only city 10 has RESIDENTIAL SALE listings

        assertEquals(10L, metrics[0].cityId)

        // All should be SALE type and RESIDENTIAL
        assertTrue(metrics.all { it.listingType == ListingType.SALE })
        assertTrue(metrics.all { it.propertyCategory == PropertyCategory.RESIDENTIAL })
    }

    @Test
    fun `dimension SELLER_AGENT with neighborhood filter`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?neighbourhood-id=1000&dimension=SELLER_AGENT",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // Agent 100 has 2 different listing types in neighborhood 1000

        // Both should be for agent 100
        assertTrue(metrics.all { it.sellerAgentUserId == 100L })
    }

    @Test
    fun `dimension aggregates metrics correctly`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=10&listing-type=SALE&dimension=CITY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size)

        val metric = metrics[0]
        assertEquals(10L, metric.cityId)
        // Should aggregate both records: id 100 (10 listings) + id 102 (8 listings) = 18 total
        assertEquals(18L, metric.total)
        // Min should be minimum of both (100000)
        assertEquals(100000L, metric.minPrice)
        // Max should be maximum of both (600000)
        assertEquals(600000L, metric.maxPrice)
        // Total price: 3000000 + 3200000 = 6200000
        assertEquals(6200000L, metric.totalPrice)
    }

    @Test
    fun `dimension UNKNOWN returns unaggregated metrics`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=SALE&dimension=UNKNOWN",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(4, metrics.size) // Same as without dimension parameter

        // Metrics should have their original dimension values, not -1
        assertTrue(metrics.any { it.neighborhoodId != -1L })
        assertTrue(metrics.any { it.cityId != -1L })
        assertTrue(metrics.any { it.sellerAgentUserId != -1L })
    }

    @Test
    fun `dimension with rental listing type`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?listing-type=RENTAL&dimension=NEIGHBORHOOD",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(1, metrics.size) // Only 1 RENTAL record

        assertEquals(1000L, metrics[0].neighborhoodId)
        assertEquals(ListingType.RENTAL, metrics[0].listingType)
    }

    @Test
    fun `dimension with multiple property categories`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics?city-id=11&listing-type=SALE&dimension=PROPERTY_CATEGORY",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // LAND and COMMERCIAL in city 11

        val categories = metrics.map { it.propertyCategory }.sortedBy { it.ordinal }
        assertEquals(listOf(PropertyCategory.LAND, PropertyCategory.COMMERCIAL), categories)
    }
}
