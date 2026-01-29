package com.wutsi.koki.listing.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingStatusRepository
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/CreateListingEndpoint.sql"])
class CreateListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Autowired
    private lateinit var statusDao: ListingStatusRepository

    private val request = CreateListingRequest(
        listingType = ListingType.SALE,
        propertyType = PropertyType.APARTMENT,
        propertyArea = 1000,
        lotArea = 2000,
        level = 1,
        unit = "a111",
        basementType = BasementType.FULL,
        parkingType = ParkingType.UNDERGROUND,
        parkings = 2,
        halfBathrooms = 1,
        bedrooms = 3,
        bathrooms = 3,
        fenceType = FenceType.CONCRETE,
        floors = 3,
        year = 1990,
        roadPavement = RoadPavement.CONCRETE,
        availableAt = DateUtils.addMonths(Date(), 3),
        distanceFromMainRoad = 150,
    )

    @Test
    fun create() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity("/v1/listings", request, CreateListingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.listingId
        val listing = dao.findById(id).get()
        assertEquals(request.listingType, listing.listingType)
        assertEquals(request.propertyType, listing.propertyType)
        assertEquals(listing.propertyType?.category, listing.propertyCategory)
        assertEquals(request.propertyArea, listing.propertyArea)
        assertEquals(request.lotArea, listing.lotArea)
        assertEquals(request.level, listing.level)
        assertEquals(request.unit?.uppercase(), listing.unit)
        assertEquals(request.basementType, listing.basementType)
        assertEquals(request.parkingType, listing.parkingType)
        assertEquals(request.parkings, listing.parkings)
        assertEquals(request.halfBathrooms, listing.halfBathrooms)
        assertEquals(request.bedrooms, listing.bedrooms)
        assertEquals(request.bathrooms, listing.bathrooms)
        assertEquals(request.fenceType, listing.fenceType)
        assertEquals(request.floors, listing.floors)
        assertEquals(request.year, listing.year)
        assertEquals(request.roadPavement, listing.roadPavement)
        assertEquals(request.distanceFromMainRoad, listing.distanceFromMainRoad)
        assertEquals(df.format(request.availableAt), df.format(listing.availableAt))
        assertEquals(USER_ID, listing.createdById)
        assertEquals(USER_ID, listing.modifiedById)
        assertEquals(TENANT_ID, listing.tenantId)
        assertEquals(ListingStatus.DRAFT, listing.status)

        val statuses = statusDao.findByListing(listing)
        assertEquals(1, statuses.size)
        assertEquals(listing.status, statuses[0].status)
        assertEquals(null, statuses[0].comment)
        assertEquals(listing.createdAt, statuses[0].createdAt)
        assertEquals(listing.createdById, statuses[0].createdById)
    }
}
