package com.wutsi.koki.webscraping.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.server.dao.ListingRepository
import com.wutsi.koki.listing.server.service.ai.AmenityResult
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingContentParserResult
import com.wutsi.koki.listing.server.service.ai.ListingLocationExtractoryResult
import com.wutsi.koki.platform.ai.agent.Agent
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.webscraping.dto.CreateWebpageListingResponse
import com.wutsi.koki.webscraping.server.dao.WebpageRepository
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import tools.jackson.databind.json.JsonMapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/webscraping/CreateWebpageListingEndpoint.sql"])
class CreateWebpageListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var webpageDao: WebpageRepository

    @Autowired
    private lateinit var listingDao: ListingRepository

    @Autowired
    private lateinit var fileDao: FileRepository

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var publisher: Publisher

    @MockitoBean
    private lateinit var agentFactory: ListingAgentFactory

    private val df = SimpleDateFormat("yyyy-MM-dd")

    private val listingParserAgent = Mockito.mock<Agent>()
    val listingContentResult = ListingContentParserResult(
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

        landTitle = true,
        technicalFile = false,
        numberOfSigners = 2,
        mutationType = MutationType.PARTIAL,
        transactionWithNotary = true,

        phone = "+2370987654321",

        furnitureType = FurnitureType.FULLY_FURNISHED,
        amenities = listOf(
            AmenityResult(id = 1103L, name = "foo"),
            AmenityResult(id = 1202L, name = "bar"),
        ),
        publicRemarks = "A beautiful apartment located in the heart of the city.",
    )

    private val listingLocationAgent = Mockito.mock<Agent>()
    private val listingLocationResult = ListingLocationExtractoryResult(
        street = "Derriere ambassade de chine",
        city = "yaounde",
        neighbourhood = "bastos",
        country = "CM",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(listingParserAgent).whenever(agentFactory).createListingContentParserAgent(any())
        doReturn(jsonMapper.writeValueAsString(listingContentResult))
            .whenever(listingParserAgent)
            .run(any())

        doReturn(listingLocationAgent).whenever(agentFactory).createListingLocationExtractoryAgent(any())
        doReturn(jsonMapper.writeValueAsString(listingLocationResult))
            .whenever(listingLocationAgent)
            .run(any())

        df.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Test
    fun create() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/100/listing", null, CreateWebpageListingResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val webpageId = response.body!!.webpageId
        val listingId = response.body!!.listingId
        val webpage = webpageDao.findById(webpageId).get()
        assertEquals(listingId, webpage.listingId)
        assertEquals(TENANT_ID, webpage.tenantId)

        val listing = listingDao.findById(listingId).get()
        assertEquals(listingContentResult.listingType, listing.listingType)
        assertEquals(listingContentResult.propertyType, listing.propertyType)
        assertEquals(listing.propertyType?.category, listing.propertyCategory)
        assertEquals(11L, listing.sellerAgentUserId)
        assertEquals(1110L, listing.cityId)
        assertEquals(listingContentResult.neighbourhoodId, listing.neighbourhoodId)
        assertEquals(USER_ID, listing.createdById)
        assertEquals(USER_ID, listing.modifiedById)
        assertEquals(TENANT_ID, listing.tenantId)
        assertEquals(ListingStatus.DRAFT, listing.status)

        val files = fileDao.countByTypeAndOwnerIdAndOwnerTypeAndDeleted(
            FileType.IMAGE,
            listingId,
            ObjectType.LISTING,
            false
        )
        assertEquals(2L, files)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher, times(2)).publish(event.capture())
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(listingId, event.firstValue.owner?.id)
        assertEquals(ObjectType.LISTING, event.firstValue.owner?.type)

        assertEquals(TENANT_ID, event.secondValue.tenantId)
        assertEquals(listingId, event.secondValue.owner?.id)
        assertEquals(ObjectType.LISTING, event.secondValue.owner?.type)
    }

    @Test
    fun `webpage with null content`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/101/listing", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.WEBPAGE_NO_CONTENT, response.body?.error?.code)
    }

    @Test
    fun `webpage with empty content`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/102/listing", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.WEBPAGE_NO_CONTENT, response.body?.error?.code)
    }

    @Test
    fun `webpage with blank content`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/103/listing", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.WEBPAGE_NO_CONTENT, response.body?.error?.code)
    }

    @Test
    fun `webpage with invalid image`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/104/listing", null, CreateWebpageListingResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val webpageId = response.body!!.webpageId
        val listingId = response.body!!.listingId
        val webpage = webpageDao.findById(webpageId).get()
        assertEquals(listingId, webpage.listingId)
        assertEquals(TENANT_ID, webpage.tenantId)

        val files = fileDao.countByTypeAndOwnerIdAndOwnerTypeAndDeleted(
            FileType.IMAGE,
            listingId,
            ObjectType.LISTING,
            false
        )
        assertEquals(1L, files)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(webpage.tenantId, event.firstValue.tenantId)
        assertEquals(webpage.listingId, event.firstValue.owner?.id)
        assertEquals(ObjectType.LISTING, event.firstValue.owner?.type)
    }

    @Test
    fun `webpage with invalid city`() {
        // GIVEN
        doReturn(jsonMapper.writeValueAsString(listingLocationResult.copy(city = "xxx")))
            .whenever(listingLocationAgent)
            .run(any())

        // WHEN
        val response = rest.postForEntity("/v1/webpages/105/listing", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LOCATION_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `listing already created`() {
        // WHEN
        val response = rest.postForEntity("/v1/webpages/106/listing", null, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.LISTING_ALREADY_CREATED, response.body?.error?.code)
    }
}
