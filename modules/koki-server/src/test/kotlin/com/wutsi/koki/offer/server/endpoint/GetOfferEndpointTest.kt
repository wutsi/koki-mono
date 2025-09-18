package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/GetOfferEndpoint.sql"])
class GetOfferEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/offers/100", GetOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val offer = response.body!!.offer
        assertEquals(11L, offer.sellerAgentUserId)
        assertEquals(22L, offer.buyerAgentUserId)
        assertEquals(33L, offer.buyerContactId)
        assertEquals(OfferStatus.SUBMITTED, offer.status)
        assertEquals(3, offer.totalVersions)
        assertEquals(333L, offer.owner?.id)
        assertEquals(ObjectType.ACCOUNT, offer.owner?.type)
        assertEquals(111L, offer.version.id)
        assertEquals("Yo man", offer.version.contingencies)
        assertEquals(OfferStatus.ACCEPTED, offer.version.status)
        assertEquals(OfferParty.BUYER, offer.version.submittingParty)
        assertEquals(10000.00, offer.version.price.amount)
        assertEquals("CAD", offer.version.price.currency)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/offers/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.OFFER_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun badTenant() {
        val response = rest.getForEntity("/v1/offers/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.OFFER_NOT_FOUND, response.body?.error?.code)
    }
}
