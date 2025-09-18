package com.wutsi.koki.offer.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.CreateOfferVersionRequest
import com.wutsi.koki.offer.dto.CreateOfferVersionResponse
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.event.OfferVersionCreatedEvent
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

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/CreateOfferVersionEndpoint.sql"])
class CreateOfferVersionEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var offerDao: OfferRepository

    @Autowired
    private lateinit var versionDao: OfferVersionRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun create() {
        val request = CreateOfferVersionRequest(
            offerId = 100,
            submittingParty = OfferParty.SELLER,
            price = 400000,
            currency = "CAD",
            contingencies = "Contengencies...",
            expiresAt = DateUtils.addDays(Date(), 3),
            closingAt = DateUtils.addMonths(Date(), 3),
        )
        val response = rest.postForEntity("/v1/offer-versions", request, CreateOfferVersionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val oldVersion = versionDao.findById(111L).get()
        assertEquals(OfferStatus.REJECTED, oldVersion.status)

        val version = versionDao.findById(response.body!!.versionId).get()
        assertEquals(TENANT_ID, version.tenantId)
        assertEquals(USER_ID, version.createdById)
        assertEquals(request.submittingParty, version.submittingParty)
        assertEquals(request.price, version.price)
        assertEquals(request.currency, version.currency)
        assertEquals(fmt.format(request.expiresAt), fmt.format(version.expiresAt))
        assertEquals(fmt.format(request.closingAt), fmt.format(version.closingAt))
        assertEquals(request.contingencies, version.contingencies)
        assertEquals(OfferStatus.SUBMITTED, version.status)
        assertEquals(22L, version.assigneeUserId)

        val offer = offerDao.findById(version.offer.id!!).get()
        assertEquals(2, offer.totalVersions)
        assertEquals(version.id, offer.version?.id)
        assertEquals(OfferStatus.SUBMITTED, offer.status)
        assertEquals(2, offer.totalVersions)

        val event = argumentCaptor<OfferVersionCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(100L, event.firstValue.offerId)
        assertEquals(version.id, event.firstValue.versionId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(111L, event.firstValue.owner?.id)
        assertEquals(ObjectType.ACCOUNT, event.firstValue.owner?.type)
    }
}
