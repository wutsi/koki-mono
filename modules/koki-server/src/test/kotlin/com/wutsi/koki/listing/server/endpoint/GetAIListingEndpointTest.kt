package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.GetAIListingResponse
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.AIListingStorage
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/GetAIListingEndpoint.sql"])
class GetAIListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var aiStorage: AIListingStorage

    @Autowired
    private lateinit var json: JsonMapper

    @Test
    fun get() {
        // GIVEN
        val request = CreateAIListingRequest(text = "This is text", cityId = 777L)
        val result = ListingContentParserResult(listingType = ListingType.SALE)
        aiStorage.store(
            request = request,
            result = result,
            listing = ListingEntity(id = 100L, tenantId = 1L)
        )

        // WHEN
        val response = rest.getForEntity("/v1/listings/100/ai", GetAIListingResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        val listing = response.body!!.aiListing
        assertEquals(-1, listing.id)
        assertEquals(100L, listing.listingId)
        assertEquals(json.writeValueAsString(request), listing.text)
        assertEquals(json.writeValueAsString(result), listing.result)
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
