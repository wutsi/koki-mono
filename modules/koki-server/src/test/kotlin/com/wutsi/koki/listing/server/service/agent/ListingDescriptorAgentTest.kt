package com.wutsi.koki.listing.server.service.agent

import com.amazonaws.util.IOUtils
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Test

class ListingDescriptorAgentTest {
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
    private val locationService = mock<LocationService>()
    private val agent = ListingDescriptorAgent(listing, images, city, neighbourhood, llm)

    @BeforeEach
    fun setUp() {
        doReturn(city).whenever(locationService).get(city.id!!)
        doReturn(neighbourhood).whenever(locationService).get(neighbourhood.id!!)
    }

    @Test
    fun tools() {
        assertEquals(0, agent.tools().size)
    }

    @Test
    fun systemInstructions() {
        assertEquals(null, agent.systemInstructions())
    }

    @Test
    fun apartment() {
        val json = agent.run(ListingDescriptorAgent.QUERY)
        val result = JsonMapper().readValue(json, ListingDescriptorAgentResult::class.java)
        assertEquals(true, result.heroImageIndex >= 0)
    }

    @Test
    fun land() {
        val json = ListingDescriptorAgent(
            listing.copy(
                propertyType = PropertyType.LAND,
                listingType = ListingType.SALE,
                bedrooms = null,
                bathrooms = null,
                furnitureType = null,
                amenities = mutableListOf(),
            ),
            images = images,
            city = city,
            neighbourhood = neighbourhood,
            llm = llm,
        ).run(ListingDescriptorAgent.QUERY)
        val result = JsonMapper().readValue(json, ListingDescriptorAgentResult::class.java)
//        assertEquals(true, result.heroImageIndex >= 0)
    }

    private fun getFile(path: String): File {
        val file = File.createTempFile("test", ".jpg")
        val fin = ListingDescriptorAgentTest::class.java.getResourceAsStream(path)
        val fout = FileOutputStream(file)
        fout.use {
            IOUtils.copy(fin, fout)
        }
        return file
    }
}
