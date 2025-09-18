package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.offer.dto.SearchOfferResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/SearchOfferEndpoint.sql"])
class SearchOfferEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by owner`() {
        val response = rest.getForEntity("/v1/offers?owner-id=111&owner-type=ACCOUNT", SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offers = response.body!!.offers
        assertEquals(1, offers.size)
        assertEquals(100L, offers[0].id)
    }

    @Test
    fun `by agent`() {
        val response =
            rest.getForEntity("/v1/offers?agent-user-id=222", SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerIds = response.body!!.offers.map { offer -> offer.id }

        assertEquals(2, offerIds.size)
        assertEquals(true, offerIds.sorted().containsAll(listOf(102L, 103L)))
    }

    @Test
    fun `by assignee`() {
        val response =
            rest.getForEntity("/v1/offers?assignee-user-id=7777", SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerIds = response.body!!.offers.map { offer -> offer.id }

        assertEquals(2, offerIds.size)
        assertEquals(true, offerIds.sorted().containsAll(listOf(101L, 103L)))
    }

    @Test
    fun `by status`() {
        val response =
            rest.getForEntity("/v1/offers?status=ACCEPTED", SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerIds = response.body!!.offers.map { offer -> offer.id }

        assertEquals(1, offerIds.size)
        assertEquals(true, offerIds.sorted().containsAll(listOf(103L)))
    }

    @Test
    fun `by ids`() {
        val response = rest.getForEntity("/v1/offers?id=101&id=102", SearchOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val offerIds = response.body!!.offers.map { offer -> offer.id }

        assertEquals(2, offerIds.size)
        assertEquals(true, offerIds.sorted().containsAll(listOf(101L, 102L)))
    }
}
