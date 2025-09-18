package com.wutsi.koki.offer.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.offer.dto.GetOfferVersionResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/GetOfferVersionEndpoint.sql"])
class GetOfferVersionEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/offer-versions/111", GetOfferVersionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val offer = response.body!!.offerVersion
        assertEquals(OfferParty.BUYER, offer.submittingParty)
        assertEquals(10000.0, offer.price.amount)
        assertEquals("CAD", offer.price.currency)
        assertEquals(OfferStatus.SUBMITTED, offer.status)
    }

    @Test
    fun notFound() {
        val response = rest.getForEntity("/v1/offer-versions/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.OFFER_VERSION_NOT_FOUND, response.body?.error?.code)
    }
}
