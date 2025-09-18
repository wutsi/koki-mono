package com.wutsi.koki.offer.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.offer.dto.UpdateOfferStatusRequest
import com.wutsi.koki.offer.dto.event.OfferStatusChangedEvent
import com.wutsi.koki.offer.server.dao.OfferRepository
import com.wutsi.koki.offer.server.dao.OfferStatusRepository
import com.wutsi.koki.offer.server.dao.OfferVersionRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/offer/UpdateOfferStatusEndpoint.sql"])
class UpdateOfferStatusEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var offerDao: OfferRepository

    @Autowired
    private lateinit var versionDao: OfferVersionRepository

    @Autowired
    private lateinit var statusDao: OfferStatusRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun status() {
        val request = UpdateOfferStatusRequest(
            status = OfferStatus.ACCEPTED,
            comment = "Oye!!!"
        )
        val response = rest.postForEntity("/v1/offers/100/status", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val offer = offerDao.findById(100L).get()
        assertEquals(request.status, offer.status)

        val version = versionDao.findById(offer.version?.id!!).get()
        assertEquals(request.status, version.status)

        val status = statusDao.findByOfferOrderByIdDesc(offer).firstOrNull()
        assertEquals(TENANT_ID, status?.tenantId)
        assertEquals(offer.id, status?.offer?.id)
        assertEquals(version.id, status?.version?.id)
        assertEquals(request.status, status?.status)
        assertEquals(request.comment, status?.comment)
        assertEquals(USER_ID, status?.createdById)

        val event = argumentCaptor<OfferStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(offer.id, event.firstValue.offerId)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(offer.ownerId, event.firstValue.owner?.id)
        assertEquals(offer.ownerType, event.firstValue.owner?.type)
    }

    @Test
    fun badStatus() {
        val request = UpdateOfferStatusRequest(
            status = OfferStatus.ACCEPTED,
            comment = "Oye!!!"
        )
        val response = rest.postForEntity("/v1/offers/110/status", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.OFFER_BAD_STATUS, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }
}
