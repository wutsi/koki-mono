package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingPriceRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingPriceEndpoint.sql"])
class UpdateListingPriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingPriceRequest(
            price = 150000,
            visitFees = 1000,
            currency = "xaf",
            sellerAgentCommission = 6.0,
            buyerAgentCommission = 3.0,
        )
        val response = rest.postForEntity("/v1/listings/$id/price", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.price, listing.price)
        assertEquals(request.visitFees, listing.visitFees)
        assertEquals(request.currency?.uppercase(), listing.currency)
        assertEquals(request.sellerAgentCommission, listing.sellerAgentCommission)
        assertEquals(request.buyerAgentCommission, listing.buyerAgentCommission)
    }
}
