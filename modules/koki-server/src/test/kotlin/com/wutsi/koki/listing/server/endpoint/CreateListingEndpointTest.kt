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
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingSequenceRepository
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.dao.ConfigurationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/CreateListingEndpoint.sql"])
class CreateListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Autowired
    private lateinit var sequenceDao: ListingSequenceRepository

    @Autowired
    private lateinit var configDao: ConfigurationRepository

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
        year = 1990
    )

    @Test
    fun create() {
        val response = rest.postForEntity("/v1/listings", request, CreateListingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.listingId
        val listing = dao.findById(id).get()
        assertEquals(request.listingType, listing.listingType)
        assertEquals(request.propertyType, listing.propertyType)
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
        assertEquals(USER_ID, listing.createdById)
        assertEquals(USER_ID, listing.modifiedById)
        assertEquals(TENANT_ID, listing.tenantId)
        assertEquals(250011L, listing.listingNumber)
        assertEquals(ListingStatus.DRAFT, listing.status)

        val seq = sequenceDao.findByTenantId(1L)
        assertEquals(11L, seq?.current)

        val config = configDao.findByTenantIdAndNameIn(1L, listOf(ConfigurationName.LISTING_START_NUMBER)).first()
        assertEquals("250000", config.value)
    }
}
