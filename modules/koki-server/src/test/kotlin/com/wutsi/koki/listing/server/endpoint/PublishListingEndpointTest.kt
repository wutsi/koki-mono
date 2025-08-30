package com.wutsi.koki.listing.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingStatusRepository
import com.wutsi.koki.listing.server.service.ListingPublisherValidator
import com.wutsi.koki.platform.mq.Publisher
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/PublishListingEndpoint.sql"])
class PublishListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    private val request = emptyMap<String, Any>()

    @Autowired
    private lateinit var statusDao: ListingStatusRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @MockitoBean
    private lateinit var validator: ListingPublisherValidator

    @Test
    fun publish() {
        val response = rest.postForEntity("/v1/listings/100/publish", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = 100L
        val listing = dao.findById(id).get()
        assertEquals(null, listing.publishedAt)
        assertEquals(ListingStatus.PUBLISHING, listing.status)

        val statuses = statusDao.findByListing(listing)
        assertEquals(1, statuses.size)
        assertEquals(listing.status, statuses[0].status)
        assertEquals(null, statuses[0].comment)
        assertEquals(USER_ID, statuses[0].createdById)

        val event = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(100L, event.firstValue.listingId)
        assertEquals(1L, event.firstValue.tenantId)
        assertEquals(ListingStatus.PUBLISHING, event.firstValue.status)
    }

    @Test
    fun `bad status`() {
        val response = rest.postForEntity("/v1/listings/101/publish", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.LISTING_INVALID_STATUS, response.body?.error?.code)
    }

    @Test
    fun `validation failure`() {
        doThrow(
            ValidationException(ErrorCode.LISTING_INVALID_IMAGE)
        ).whenever(validator).validate(any())

        val response = rest.postForEntity("/v1/listings/102/publish", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.LISTING_INVALID_IMAGE, response.body?.error?.code)
    }
}
