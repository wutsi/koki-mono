package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingLeasingRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingLeasingEndpoint.sql"])
class UpdateListingLeasingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingLeasingRequest(
            leaseTerm = 1,
            noticePeriod = 1,
            securityDeposit = 1000000,
            advanceRent = 3,
        )
        val response = rest.postForEntity("/v1/listings/$id/leasing", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.leaseTerm, listing.leaseTerm)
        assertEquals(request.noticePeriod, listing.noticePeriod)
        assertEquals(request.advanceRent, listing.advanceRent)
        assertEquals(request.securityDeposit, listing.securityDeposit)
    }
}
