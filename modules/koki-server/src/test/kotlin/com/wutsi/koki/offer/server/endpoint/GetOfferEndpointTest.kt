package com.wutsi.koki.offer.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.CreateOfferResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
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

@Sql(value = ["/db/test/clean.sql"])
class CreateOfferEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var offerDao: OfferRepository

    @Autowired
    private lateinit var versionDao: OfferVersionRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun create() {
        val request = CreateOfferRequest(
            owner = ObjectReference(111, ObjectType.LISTING),
            sellerAgentUserId = 111L,
            buyerContactId = 300L,
            buyerAgentUserId = 333L,
            submittingParty = OfferParty.BUYER,
            price = 400000,
            currency = "CAD",
            contingencies = "Contengencies...",
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
        )
        val response = rest.postForEntity("/v1/offers", request, CreateOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val offer = offerDao.findById(response.body!!.offerId).get()
        assertEquals(TENANT_ID, offer.tenantId)
        assertEquals(USER_ID, offer.createdById)
        assertEquals(request.sellerAgentUserId, offer.sellerAgentUserId)
        assertEquals(request.buyerAgentUserId, offer.buyerAgentUserId)
        assertEquals(request.buyerContactId, offer.buyerContactId)
        assertEquals(OfferStatus.SUBMITTED, offer.status)
        assertEquals(1, offer.totalVersions)
        assertNotNull(offer.version)

        val version = versionDao.findById(offer.version?.id!!).get()
        assertEquals(TENANT_ID, version.tenantId)
        assertEquals(USER_ID, version.createdById)
        assertEquals(request.submittingParty, version.submittingParty)
        assertEquals(request.price, version.price)
        assertEquals(request.currency, version.currency)
        assertEquals(fmt.format(request.expiresAt), fmt.format(version.expiresAt))
        assertEquals(fmt.format(request.closingAt), fmt.format(version.closingAt))
        assertEquals(request.contingencies, version.contingencies)

        val event = argumentCaptor<OfferSubmittedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(offer.id, event.firstValue.offerId)
        assertEquals(version.id, event.firstValue.versionId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(request.owner?.id, event.firstValue.owner?.id)
        assertEquals(request.owner?.type, event.firstValue.owner?.type)
    }

}
