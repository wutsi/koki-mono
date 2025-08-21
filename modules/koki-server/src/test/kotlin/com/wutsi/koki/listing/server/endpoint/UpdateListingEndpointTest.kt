package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.UpdateListingRequest
import com.wutsi.koki.listing.server.dao.ListingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingEndpoint.sql"])
class UpdateListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    private val request = UpdateListingRequest(
        listingType = ListingType.SALE,
        propertyType = PropertyType.APARTMENT,
        propertyArea = 1000,
        lotArea = 2000,
        level = 1,
        unit = "111",
        basementType = BasementType.FULL,
        parkingType = ParkingType.UNDERGROUND,
        parkings = 2,
        halfBathrooms = 1,
        bedrooms = 3,
        bathrooms = 3,
        fenceType = FenceType.CONCRETE,
        floors = 3,
        year = 1990
    )

    @Test
    fun update() {
        val response = rest.postForEntity("/v1/listings/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = 100L
        val listing = dao.findById(id).get()
        assertEquals(request.listingType, listing.listingType)
        assertEquals(request.propertyType, listing.propertyType)
        assertEquals(request.propertyArea, listing.propertyArea)
        assertEquals(request.lotArea, listing.lotArea)
        assertEquals(request.level, listing.level)
        assertEquals(request.unit, listing.unit)
        assertEquals(request.basementType, listing.basementType)
        assertEquals(request.parkingType, listing.parkingType)
        assertEquals(request.parkings, listing.parkings)
        assertEquals(request.halfBathrooms, listing.halfBathrooms)
        assertEquals(request.bedrooms, listing.bedrooms)
        assertEquals(request.bathrooms, listing.bathrooms)
        assertEquals(request.fenceType, listing.fenceType)
        assertEquals(request.floors, listing.floors)
        assertEquals(request.year, listing.year)
        assertEquals(USER_ID, listing.modifiedById)
    }

    @Test
    fun `not found`() {
        val response = rest.postForEntity("/v1/listings/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LISTING_NOT_FOUND, response.body?.error?.code)
    }
}
