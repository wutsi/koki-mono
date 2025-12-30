package com.wutsi.koki.listing.server.service.ai

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test

class ListingContentGeneratorAgentTest {
    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )
    private val city = LocationEntity(id = 111, name = "Yaounde", country = "CM")
    private val neighbourhood = LocationEntity(id = 222, name = "Bastos", country = "CM")
    private val listing = ListingEntity(
        listingType = ListingType.RENTAL,
        propertyType = PropertyType.APARTMENT,
        bedrooms = 2,
        bathrooms = 1,
        furnitureType = FurnitureType.FULLY_FURNISHED,
        cityId = city.id,
        neighbourhoodId = neighbourhood.id,
        lotArea = 1500,
        amenities = mutableListOf(
            AmenityEntity(name = "Electricity"),
            AmenityEntity(name = "Running Water"),
            AmenityEntity(name = "BBQ"),
            AmenityEntity(name = "Refrigerator"),
            AmenityEntity(name = "Microwave"),
            AmenityEntity(name = "Stove"),
            AmenityEntity(name = "Dishwasher"),
            AmenityEntity(name = "Dinning table"),
        )
    )
    val images = listOf(
        FileEntity(id = 1, description = "Front view of the apartment building"),
        FileEntity(id = 2, description = "Master bedroom with king-size bed and balcony"),
        FileEntity(id = 3, description = "Modern kitchen with appliances"),
        FileEntity(id = 4, description = "Spacious living room with natural light"),
        FileEntity(id = 5, description = "Bathroom with shower and bathtub"),
    )
    val landImages = listOf(
        FileEntity(id = 1, description = "Areal view of the land plot"),
        FileEntity(id = 2, description = "West side of the land showing greenery"),
        FileEntity(id = 3, description = "Font view of the land with access road"),
    )

    private val locationService = mock<LocationService>()
    private val agent = ListingContentGeneratorAgent(listing, images, city, neighbourhood, llm)

    @BeforeEach
    fun setUp() {
        doReturn(city).whenever(locationService).get(city.id!!)
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
    }

    @Test
    fun apartment() {
        val json = agent.run(ListingContentGeneratorAgent.QUERY)
        val result = JsonMapper().readValue(json, ListingContentGeneratorResult::class.java)
        assertEquals(true, result.heroImageIndex >= 0)
        assertNotNull(result.summary)
        assertNotNull(result.description)
        assertNotNull(result.summaryFr)
        assertNotNull(result.descriptionFr)
    }

    @Test
    fun land() {
        val json = ListingContentGeneratorAgent(
            listing.copy(
                propertyType = PropertyType.LAND,
                listingType = ListingType.SALE,
                bedrooms = null,
                bathrooms = null,
                furnitureType = null,
                amenities = mutableListOf(),
            ),
            images = landImages,
            city = city,
            neighbourhood = neighbourhood,
            llm = llm,
        ).run(ListingContentGeneratorAgent.QUERY)
        val result = JsonMapper().readValue(json, ListingContentGeneratorResult::class.java)
        assertEquals(false, result.title?.contains("Titled", true))
        assertEquals(false, result.titleFr?.contains("titré", true))
        assertNotNull(result.summary)
        assertNotNull(result.description)
        assertNotNull(result.summaryFr)
        assertNotNull(result.descriptionFr)
    }

    @Test
    fun `lang with perfect legal context`() {
        val json = ListingContentGeneratorAgent(
            listing.copy(
                propertyType = PropertyType.LAND,
                listingType = ListingType.SALE,
                bedrooms = null,
                bathrooms = null,
                furnitureType = null,
                amenities = mutableListOf(),
                numberOfSigners = 1,
                landTitle = true,
                technicalFile = true,
                transactionWithNotary = true,
                mutationType = MutationType.TOTAL,
                subdivided = true,
                morcelable = true,
            ),
            images = landImages,
            city = city,
            neighbourhood = neighbourhood,
            llm = llm,
        ).run(ListingContentGeneratorAgent.QUERY)
        val result = JsonMapper().readValue(json, ListingContentGeneratorResult::class.java)
        assertEquals(true, result.title?.contains("Titled", true))
        assertEquals(true, result.titleFr?.contains("titré", true))
        assertEquals(true, result.titleFr?.contains("loti", true))
        assertNotNull(result.summary)
        assertNotNull(result.description)
        assertNotNull(result.summaryFr)
        assertNotNull(result.descriptionFr)
    }
}
