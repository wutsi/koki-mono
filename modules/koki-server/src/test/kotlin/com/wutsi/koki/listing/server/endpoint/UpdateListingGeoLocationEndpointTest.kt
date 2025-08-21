package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.UpdateListingGeoLocationRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.refdata.dto.GeoLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingGeoLocationEndpoint.sql"])
class UpdateListingGeoLocationEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun update() {
        val id = 100L
        val request = UpdateListingGeoLocationRequest(
            geoLocation = GeoLocation(
                latitude = 11.1111,
                longitude = 2.2222,
            )
        )
        val response = rest.postForEntity("/v1/listings/$id/geo-location", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val listing = dao.findById(id).get()
        assertEquals(request.geoLocation?.latitude, listing.latitude)
        assertEquals(request.geoLocation?.longitude, listing.longitude)
    }
}
