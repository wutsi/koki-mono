package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingSellerRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.refdata.dto.IDType
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
            sellerName = "Ray sponsible",
            sellerEmail = "ray.sponsible@gmail.com",
            sellerPhone = "+15147589999",
            sellerIdType = IDType.CNI,
            sellerIdNumber = "123ab44",
            sellerIdCountry = "CA",
        )
        val response = rest.postForEntity("/v1/listings/$id/seller", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.sellerName?.uppercase(), listing.sellerName)
        assertEquals(request.sellerEmail?.lowercase(), listing.sellerEmail)
        assertEquals(request.sellerPhone, listing.sellerPhone)
        assertEquals(request.sellerIdType, listing.sellerIdType)
        assertEquals(request.sellerIdNumber, listing.sellerIdNumber)
        assertEquals(request.sellerIdCountry?.lowercase(), listing.sellerIdCountry)
    }

    @Test
    fun `no id`() {
        val id = 100L
        val request = UpdateListingSellerRequest(
            sellerName = "Ray sponsible",
            sellerEmail = "ray.sponsible@gmail.com",
            sellerPhone = "+15147589999",
            sellerIdType = null,
            sellerIdNumber = null,
            sellerIdCountry = null,
        )
        val response = rest.postForEntity("/v1/listings/$id/seller", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.sellerName?.uppercase(), listing.sellerName)
        assertEquals(request.sellerEmail?.lowercase(), listing.sellerEmail)
        assertEquals(request.sellerPhone, listing.sellerPhone)
        assertEquals(null, listing.sellerIdType)
        assertEquals(null, listing.sellerIdNumber)
        assertEquals(null, listing.sellerIdCountry)
    }
}
