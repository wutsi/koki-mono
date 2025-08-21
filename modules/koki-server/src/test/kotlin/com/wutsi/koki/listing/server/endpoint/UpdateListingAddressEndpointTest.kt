package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingAddressRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.refdata.dto.Address
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingAddressEndpoint.sql"])
class UpdateListingAddressEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingAddressRequest(
            address = Address(
                country = "CA",
                neighborhoodId = 111L,
                cityId = 222L,
                street = "111 Linton",
                postalCode = "H1x 1h1",
            )
        )
        val response = rest.postForEntity("/v1/listings/$id/address", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.address?.country?.lowercase(), listing.country)
        assertEquals(request.address?.cityId, listing.cityId)
        assertEquals(200L, listing.stateId)
        assertEquals(request.address?.neighborhoodId, listing.neighbourhoodId)
        assertEquals(request.address?.street, listing.street)
        assertEquals(request.address?.postalCode?.uppercase(), listing.postalCode)
    }

    @Test
    fun `empty address`() {
        val id = 100L
        val request = UpdateListingAddressRequest(
            address = Address(
                country = "",
                neighborhoodId = null,
                cityId = null,
                street = "",
                postalCode = "",
            )
        )
        val response = rest.postForEntity("/v1/listings/$id/address", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(null, listing.country)
        assertEquals(null, listing.cityId)
        assertEquals(null, listing.neighbourhoodId)
        assertEquals(null, listing.street)
        assertEquals(null, listing.postalCode)
    }
}
