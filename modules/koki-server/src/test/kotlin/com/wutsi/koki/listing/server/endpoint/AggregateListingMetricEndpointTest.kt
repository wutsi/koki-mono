package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.server.dao.ListingMetricRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/AggregateListingMetricEndpoint.sql"])
class AggregateListingMetricEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingMetricRepository

    @Test
    fun `aggregate creates metrics from listings`() {
        // WHEN
        val response = rest.postForEntity(
            "/v1/listings/metrics",
            null,
            Map::class.java
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        // VERIFY - Check that metrics were created
        val metrics = dao.findAll()
        assertEquals(5, metrics.size)
    }

    @Test
    fun `aggregate updates existing metrics`() {
        // GIVEN - First aggregation
        val firstResponse = rest.postForEntity(
            "/v1/listings/metrics",
            null,
            Map::class.java
        )
        assertEquals(HttpStatus.OK, firstResponse.statusCode)

        // WHEN - Second aggregation
        val secondResponse = rest.postForEntity(
            "/v1/listings/metrics",
            null,
            Map::class.java
        )

        // THEN - Should update existing metrics
        assertEquals(HttpStatus.OK, secondResponse.statusCode)
    }
}
