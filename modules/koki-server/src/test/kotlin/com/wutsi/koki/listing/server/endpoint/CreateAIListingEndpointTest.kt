package com.wutsi.koki.listing.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.CreateAIListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.server.dao.AIListingRepository
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.dao.ListingStatusRepository
import com.wutsi.koki.listing.server.service.ai.AmenityResult
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.platform.ai.agent.Agent
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import tools.jackson.databind.json.JsonMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/listing/CreateAIListingEndpoint.sql"])
class CreateAIListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Autowired
    private lateinit var statusDao: ListingStatusRepository

    @Autowired
    private lateinit var aiListingDao: AIListingRepository

    @Autowired
    private lateinit var ds: DataSource

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var agentFactory: ListingAgentFactory

    private val agent = mock<Agent>()

    private val request = CreateAIListingRequest(
        text = "Beautiful 3-bedroom apartment for sale in downtown.",
        cityId = 1110L,
    )

    private val df = SimpleDateFormat("yyyy-MM-dd")

    @BeforeEach
    override fun setUp() {
        super.setUp()

        df.timeZone = TimeZone.getTimeZone("UTC")
        doReturn(agent).whenever(agentFactory).createListingContentParserAgent(any())
    }

    @Test
    fun create() {
        val result = ListingContentParserResult(
            valid = true,
            listingType = ListingType.SALE,
            propertyType = PropertyType.APARTMENT,
            propertyArea = 1000,
            lotArea = 2000,
            level = 1,
            basementType = BasementType.FULL,
            parkingType = ParkingType.UNDERGROUND,
            parkings = 2,
            halfBathrooms = 1,
            bedrooms = 4,
            bathrooms = 3,
            fenceType = FenceType.CONCRETE,
            floors = 3,
            year = 1990,
            roadPavement = RoadPavement.CONCRETE,
            availableAt = DateUtils.addMonths(Date(), 3),
            distanceFromMainRoad = 150,

            street = "Derriere ambassade de chine",
            city = "yaounde",
            neighbourhood = "bastos",
            neighbourhoodId = 5555L,
            country = "CM",

            price = 350000,
            visitFees = 5000,
            currency = "XAF",

            leaseTerm = 12,
            noticePeriod = 3,
            advanceRent = 2,
            securityDeposit = 3,

            phone = "+2370987654321",

            furnitureType = FurnitureType.FULLY_FURNISHED,
            amenities = listOf(
                AmenityResult(id = 1103L, name = "foo"),
                AmenityResult(id = 1202L, name = "bar"),
            ),
            publicRemarks = "A beautiful apartment located in the heart of the city.",
        )
        val json = jsonMapper.writeValueAsString(result)
        doReturn(json).whenever(agent).run(any())

        val response = rest.postForEntity("/v1/listings/ai", request, CreateListingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.listingId
        val listing = dao.findById(id).get()
        assertEquals(result.listingType, listing.listingType)
        assertEquals(result.propertyType, listing.propertyType)
        assertEquals(result.propertyArea, listing.propertyArea)
        assertEquals(result.lotArea, listing.lotArea)
        assertEquals(result.level, listing.level)
        assertEquals(null, listing.unit)
        assertEquals(result.basementType, listing.basementType)
        assertEquals(result.parkingType, listing.parkingType)
        assertEquals(result.parkings, listing.parkings)
        assertEquals(result.halfBathrooms, listing.halfBathrooms)
        assertEquals(result.bedrooms, listing.bedrooms)
        assertEquals(result.bathrooms, listing.bathrooms)
        assertEquals(result.fenceType, listing.fenceType)
        assertEquals(result.floors, listing.floors)
        assertEquals(result.year, listing.year)
        assertEquals(result.roadPavement, listing.roadPavement)
        assertEquals(result.distanceFromMainRoad, listing.distanceFromMainRoad)
        assertEquals(df.format(result.availableAt), df.format(listing.availableAt))
        assertEquals(result.street, listing.street)
        assertEquals(request.cityId, listing.cityId)
        assertEquals(result.neighbourhoodId, listing.neighbourhoodId)
        assertEquals(result.country, listing.country?.uppercase())
        assertEquals(result.price, listing.price)
        assertEquals(result.visitFees, listing.visitFees)
        assertEquals(result.currency, listing.currency)
        assertEquals(result.leaseTerm, listing.leaseTerm)
        assertEquals(result.securityDeposit, listing.securityDeposit)
        assertEquals(result.noticePeriod, listing.noticePeriod)
        assertEquals(result.advanceRent, listing.advanceRent)
        assertEquals(result.furnitureType, listing.furnitureType)
        assertEquals(result.amenities.map { it.id }, getAmenityIds(id))
        assertEquals(result.publicRemarks, listing.publicRemarks)
        assertEquals(USER_ID, listing.createdById)
        assertEquals(USER_ID, listing.modifiedById)
        assertEquals(TENANT_ID, listing.tenantId)
        assertEquals(ListingStatus.DRAFT, listing.status)

        val ai = aiListingDao.findByListing(listing)
        assertEquals(request.text, ai?.text)
        assertEquals(result, jsonMapper.readValue(ai?.result, ListingContentParserResult::class.java))
        assertEquals(listing.createdAt, ai?.createdAt)

        val statuses = statusDao.findByListing(listing)
        assertEquals(1, statuses.size)
        assertEquals(listing.status, statuses[0].status)
        assertEquals(null, statuses[0].comment)
        assertEquals(listing.createdAt, statuses[0].createdAt)
        assertEquals(listing.createdById, statuses[0].createdById)
    }

    @Test
    fun invalid() {
        val result = ListingContentParserResult(
            valid = false,
            reason = "The description provided is too vague to extract listing details.",
        )
        val json = jsonMapper.writeValueAsString(result)
        doReturn(json).whenever(agent).run(any())

        val response = rest.postForEntity("/v1/listings/ai", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.LISTING_INVALID_TEXT, response.body?.error?.code)
    }

    private fun getAmenityIds(listingId: Long): List<Long> {
        val result = mutableListOf<Long>()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery("SELECT amenity_fk FROM T_LISTING_AMENITY where listing_fk=$listingId")
                rs.use {
                    while (rs.next()) {
                        result.add(rs.getLong(1))
                    }
                }
            }
        }
        return result.sorted()
    }
}
