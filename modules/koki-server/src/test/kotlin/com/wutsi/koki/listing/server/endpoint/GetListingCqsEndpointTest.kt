package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.GetListingCqsResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/GetListingCqsEndpoint.sql"])
class GetListingCqsEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun getCqs() {
        val response = rest.getForEntity("/v1/listings/100/cqs", GetListingCqsResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val body = response.body
        assertNotNull(body)
        assertEquals(100L, body.listingId)
        assertTrue(body.overallCqs >= 0)
        assertTrue(body.overallCqs <= 100)

        // Verify breakdown exists
        val breakdown = body.cqsBreakdown
        assertNotNull(breakdown)
        assertTrue(breakdown.total >= 0)
        assertTrue(breakdown.total <= 100)

        // Verify all category scores
        assertNotNull(breakdown.general)
        assertTrue(breakdown.general.score >= 0)
        assertTrue(breakdown.general.score <= breakdown.general.max)

        assertNotNull(breakdown.legal)
        assertTrue(breakdown.legal.score >= 0)
        assertTrue(breakdown.legal.score <= breakdown.legal.max)

        assertNotNull(breakdown.amenities)
        assertTrue(breakdown.amenities.score >= 0)
        assertTrue(breakdown.amenities.score <= breakdown.amenities.max)

        assertNotNull(breakdown.address)
        assertTrue(breakdown.address.score >= 0)
        assertTrue(breakdown.address.score <= breakdown.address.max)

        assertNotNull(breakdown.geo)
        assertTrue(breakdown.geo.score >= 0)
        assertTrue(breakdown.geo.score <= breakdown.geo.max)

        assertNotNull(breakdown.rental)
        assertTrue(breakdown.rental.score >= 0)
        assertTrue(breakdown.rental.score <= breakdown.rental.max)

        assertNotNull(breakdown.images)
        assertTrue(breakdown.images.score >= 0)
        assertTrue(breakdown.images.score <= breakdown.images.max)

        // Verify overall equals sum of parts
        assertEquals(breakdown.total, body.overallCqs)
    }

    @Test
    fun listingNotFound() {
        val response = rest.getForEntity("/v1/listings/999/cqs", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/listings/200/cqs", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }
}
