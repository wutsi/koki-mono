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

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/SearchNeighbourhoodMetricEndpoint.sql"])
class SearchNeighbourhoodMetricEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity(
            "/v1/listings/metrics/neighbourhoods",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(5, metrics.size)
    }

    @Test
    fun `by neighbourhood-id`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=1000",
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
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=9999",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(0, metrics.size)
    }

    @Test
    fun `by property-category RESIDENTIAL`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics/neighbourhoods?property-category=RESIDENTIAL",
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
            "/v1/listings/metrics/neighbourhoods?property-category=LAND",
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
            "/v1/listings/metrics/neighbourhoods?property-category=COMMERCIAL",
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
            "/v1/listings/metrics/neighbourhoods?listing-type=SALE",
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
            "/v1/listings/metrics/neighbourhoods?listing-type=RENTAL",
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
            "/v1/listings/metrics/neighbourhoods?listing-status=SOLD",
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
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=1000&property-category=RESIDENTIAL",
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
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=1000&listing-type=SALE",
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
            "/v1/listings/metrics/neighbourhoods?property-category=RESIDENTIAL&listing-type=SALE",
            SearchListingMetricResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val metrics = response.body!!.metrics
        assertEquals(2, metrics.size) // IDs 100 and 102 have RESIDENTIAL + SALE
        assertTrue(metrics.all { it.propertyCategory == PropertyCategory.RESIDENTIAL && it.listingType == ListingType.SALE })
    }

    @Test
    fun `with multiple filters`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=1000&property-category=RESIDENTIAL&listing-type=SALE&listing-status=SOLD",
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
            "/v1/listings/metrics/neighbourhoods?neighbourhood-id=1000&listing-type=SALE",
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
        assertEquals(500L, metric.averageLotArea.toLong())
        assertEquals(600.0, metric.pricePerSquareMeter)
        assertEquals(3000000L, metric.totalPrice)
        assertEquals("XAF", metric.currency)
    }

    @Test
    fun `verify all neighbourhoods are returned`() {
        val response = rest.getForEntity(
            "/v1/listings/metrics/neighbourhoods?listing-type=SALE",
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
            "/v1/listings/metrics/neighbourhoods",
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
}
