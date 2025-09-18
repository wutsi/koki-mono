package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.offer.dto.SearchOfferVersionResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/SearchOfferVersionEndpoint.sql"])
class SearchOfferVersionEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by offer-id`() {
        val response = rest.getForEntity("/v1/offer-versions?offer-id=100", SearchOfferVersionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerVersionIds = response.body!!.offerVersions.map { version -> version.id }
        assertEquals(2, offerVersionIds.size)
        assertEquals(true, offerVersionIds.contains(1001L))
        assertEquals(true, offerVersionIds.contains(1002L))
    }

    @Test
    fun `by agent`() {
        val response = rest.getForEntity("/v1/offer-versions?agent-user-id=111", SearchOfferVersionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerVersionIds = response.body!!.offerVersions.map { version -> version.id }
        assertEquals(2, offerVersionIds.size)
        assertEquals(true, offerVersionIds.contains(1101L))
        assertEquals(true, offerVersionIds.contains(1201L))
    }

    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/offer-versions?id=1101&id=1001", SearchOfferVersionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerVersionIds = response.body!!.offerVersions.map { version -> version.id }
        assertEquals(2, offerVersionIds.size)
        assertEquals(true, offerVersionIds.contains(1101L))
        assertEquals(true, offerVersionIds.contains(1001L))
    }
}
