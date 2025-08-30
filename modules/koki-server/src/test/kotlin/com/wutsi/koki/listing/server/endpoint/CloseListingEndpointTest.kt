package com.wutsi.koki.listing.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.CloseListingRequest
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingStatusRepository
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/CloseListingEndpoint.sql"])
class CloseListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Autowired
    private lateinit var statusDao: ListingStatusRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val df = SimpleDateFormat("yyyy-MM-dd")

    @BeforeEach
    override fun setUp() {
        super.setUp()

        df.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Test
    fun sold() {
        close(100, ListingStatus.SOLD)
    }

    @Test
    fun rented() {
        close(101, ListingStatus.RENTED)
    }

    @Test
    fun cancelled() {
        close(102, ListingStatus.CANCELLED)
    }

    @Test
    fun withdawn() {
        close(102, ListingStatus.WITHDRAWN)
    }

    @Test
    fun expired() {
        close(103, ListingStatus.EXPIRED)
    }

    @Test
    fun `invalid close status DRAFT`() {
        invalidStatus(200, ListingStatus.DRAFT)
    }

    @Test
    fun `invalid close status ACTIVE`() {
        invalidStatus(200, ListingStatus.ACTIVE)
    }

    @Test
    fun `invalid close status PUBLISHING`() {
        invalidStatus(200, ListingStatus.PUBLISHING)
    }

    @Test
    fun `listing not active`() {
        invalidStatus(201, ListingStatus.SOLD)
    }

    @Test
    fun `sold - property for rental`() {
        invalidStatus(202, ListingStatus.SOLD)
    }

    @Test
    fun `rented - property for sale`() {
        invalidStatus(203, ListingStatus.RENTED)
    }

    private fun close(id: Long, status: ListingStatus) {
        val request = CloseListingRequest(
            status = status,
            buyerName = "Ray Sponsible",
            buyerEmail = "ray.sponsible@gmail.com",
            buyerPhone = "+15147580000",
            transactionDate = df.parse("2020-03-05"),
            transactionPrice = 150000,
            buyerAgentUserId = 111L,
            comment = "Yeeesss!!"
        )
        val response = rest.postForEntity("/v1/listings/$id/close", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertNotNull(listing.closedAt)
        assertEquals(request.status, listing.status)
        assertEquals(request.buyerPhone, listing.buyerPhone)
        assertEquals(request.buyerEmail, listing.buyerEmail)
        assertEquals("2020-03-05", df.format(listing.transactionDate))
        assertEquals(request.transactionPrice, listing.transactionPrice)
        assertEquals(request.buyerAgentUserId, listing.buyerAgentUserId)

        val statuses = statusDao.findByListing(listing)
        assertEquals(1, statuses.size)
        assertEquals(listing.status, statuses[0].status)
        assertEquals(request.comment, statuses[0].comment)
        assertEquals(USER_ID, statuses[0].createdById)

        val event = argumentCaptor<ListingStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(listing.id!!, event.firstValue.listingId)
        assertEquals(listing.tenantId, event.firstValue.tenantId)
        assertEquals(request.status, event.firstValue.status)
    }

    private fun invalidStatus(id: Long, status: ListingStatus) {
        val request = CloseListingRequest(
            status = status,
            buyerName = "Ray Sponsible",
            buyerEmail = "ray.sponsible@gmail.com",
            buyerPhone = "+15147580000",
            transactionDate = df.parse("2020-03-05"),
            buyerAgentUserId = 111L,
            comment = "Yeeesss!!"
        )
        val response = rest.postForEntity("/v1/listings/$id/close", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.LISTING_INVALID_STATUS, response.body?.error?.code)
    }
}
