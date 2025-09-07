package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingSellerEndpoint.sql"])
class UpdateListingSellerEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingSellerRequest(
            sellerContactId = 111L,
        )
        val response = rest.postForEntity("/v1/listings/$id/seller", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.sellerContactId, listing.sellerContactId)
    }
}
