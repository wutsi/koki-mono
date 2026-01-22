package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.GetAIListingResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/GetAIListingEndpoint.sql"])
class GetAIListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/listings/100/ai", GetAIListingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val listing = response.body!!.aiListing
        assertEquals(111L, listing.id)
        assertEquals(100L, listing.listingId)
        assertEquals("This is text", listing.text)
        assertEquals("{\"foo\": \"bar\"}", listing.result)
    }

    @Test
    fun `listing not found`() {
        val response = rest.getForEntity("/v1/listings/999/ai", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `ai listing not found`() {
        val response = rest.getForEntity("/v1/listings/110/ai", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_AI_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun anotherTenant() {
        val response = rest.getForEntity("/v1/listings/200/ai", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }
}
