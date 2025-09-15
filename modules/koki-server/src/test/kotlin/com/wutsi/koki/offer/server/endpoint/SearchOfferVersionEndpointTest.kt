package com.wutsi.koki.offer.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.GetOfferResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.SearchOfferResponse
import com.wutsi.koki.offer.dto.event.OfferSubmittedEvent
import com.wutsi.koki.offer.server.dao.OfferRepository
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.platform.mq.Publisher
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
