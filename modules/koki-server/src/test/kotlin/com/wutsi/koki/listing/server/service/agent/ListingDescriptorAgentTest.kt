package com.wutsi.koki.listing.server.service.agent

import com.amazonaws.util.IOUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.LocationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import java.io.FileOutputStream
import kotlin.test.Ignore
import kotlin.test.Test

class ListingDescriptorAgentTest {
    private val llm = Gemini(
        apiKey = System.getenv("GEMINI_API_KEY"),
        model = "gemini-2.5-flash",
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
    private val files = listOf(
        getFile("/fs/listing/room.jpg"),
        getFile("/fs/listing/room-1.jpg"),
        getFile("/fs/listing/room-2.jpg"),
        getFile("/fs/listing/room-3.jpg"),
        getFile("/fs/listing/room-4.jpg"),
        getFile("/fs/listing/room-5.jpg"),
        getFile("/fs/listing/room-6.jpg"),
        getFile("/fs/listing/room-7.jpg"),
    )
    private val locationService = mock<LocationService>()
    private val agent = ListingDescriptorAgent(listing, locationService, llm, 5)

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
    @Ignore("Because of rate limit")
    fun run() {
        val json = agent.run(ListingDescriptorAgent.QUERY, files)
        val result = ObjectMapper().readValue(json, ListingDescriptorAgentResult::class.java)
        assertEquals(true, result.heroImageIndex >= 0)
    }

    @Test
    @Ignore("Because of rate limit")
    fun land() {
        val xagent = ListingDescriptorAgent(
            listing.copy(propertyType = PropertyType.LAND, listingType = ListingType.SALE),
            locationService, llm, 5
        )
        val json = xagent.run(ListingDescriptorAgent.QUERY, files)
        val result = ObjectMapper().readValue(json, ListingDescriptorAgentResult::class.java)
//        assertEquals(true, result.heroImageIndex >= 0)
    }

    @Test
    @Ignore("Because of rate limit")
    fun `no image`() {
        val json = agent.run(ListingDescriptorAgent.QUERY, emptyList<File>())
        val result = ObjectMapper().readValue(json, ListingDescriptorAgentResult::class.java)
        assertEquals(true, result.heroImageIndex <= 0)
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
