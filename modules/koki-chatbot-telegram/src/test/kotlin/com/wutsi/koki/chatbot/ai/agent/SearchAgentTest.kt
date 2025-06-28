package com.wutsi.koki.chatbot.ai.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchAgentTest {
    private val kokiRooms = mock<KokiRooms>()
    private val kokiRefData = mock<KokiRefData>()
    private val objectMapper = ObjectMapper()

    private val tool = SearchRoomTool(
        kokiRooms = kokiRooms,
        kokiRefData = kokiRefData,
        objectMapper = objectMapper,
    )

    private val llm = Deepseek(
        apiKey = System.getenv("DEEPSEEK_API_KEY"),
        model = "deepseek-chat",
    )

    @Test
    fun `search in city`() {
        val neighborhood = createLocation(id = 11L, name = "Bastos", type = LocationType.NEIGHBORHOOD)
        val city = createLocation(id = 22L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        val rooms = listOf(
            createRoom(id = 1, numberOfRooms = 2, numberOfBathrooms = 1, city = city, type = RoomType.APARTMENT),
            createRoom(id = 2, numberOfRooms = 3, numberOfBathrooms = 2, city = city, type = RoomType.APARTMENT),
            createRoom(
                id = 3,
                numberOfRooms = 4,
                numberOfBathrooms = 3,
                city = city,
                neighborhood = neighborhood,
                type = RoomType.APARTMENT
            ),
        )
        setupRooms(rooms)

        val json = SearchAgent(llm, tool).run("I look for apartment in Yaounde")

        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        assertEquals(rooms.size, result.properties.size)
        assertEquals(rooms[0].listingUrl, result.properties[0].url)
        assertEquals(rooms[1].listingUrl, result.properties[1].url)
        assertEquals(rooms[2].listingUrl, result.properties[2].url)
        assertEquals(city.id, result.searchParameters.cityId)
        assertEquals(null, result.searchParameters.neighborhoodId)
        assertEquals("APARTMENT", result.searchParameters.propertyType)
        assertEquals(null, result.searchParameters.minBedrooms)
        assertEquals(null, result.searchParameters.maxBedrooms)
        assertEquals(null, result.searchParameters.minBudget)
        assertEquals(null, result.searchParameters.maxBudget)
    }

    @Test
    fun `search in neighborhood`() {
        val neighborhood = createLocation(id = 22L, name = "Bastos", type = LocationType.NEIGHBORHOOD)
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(neighborhood, city)

        val rooms = listOf(
            createRoom(
                id = 1,
                numberOfRooms = 2,
                numberOfBathrooms = 1,
                city = city,
                neighborhood = neighborhood,
                type = RoomType.APARTMENT
            ),
            createRoom(
                id = 2,
                numberOfRooms = 3,
                numberOfBathrooms = 2,
                city = city,
                neighborhood = neighborhood,
                type = RoomType.HOUSE
            ),
            createRoom(
                id = 3,
                numberOfRooms = 4,
                numberOfBathrooms = 3,
                city = city,
                neighborhood = neighborhood,
                type = RoomType.HOUSE
            ),
        )
        setupRooms(rooms)

        val json = SearchAgent(llm, tool)
            .run("I look for a 3 bedrooms in Bastos, Yaounde")

        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        assertEquals(2, result.properties.size)
        assertEquals(rooms[1].listingUrl, result.properties[0].url)
        assertEquals(city.id, result.searchParameters.cityId)
        assertEquals(neighborhood.id, result.searchParameters.neighborhoodId)
        assertEquals(null, result.searchParameters.propertyType)
        assertEquals(3, result.searchParameters.minBedrooms)
        assertEquals(null, result.searchParameters.maxBedrooms)
        assertEquals(null, result.searchParameters.minBudget)
        assertEquals(null, result.searchParameters.maxBudget)
    }

    @Test
    fun `search in width budget`() {
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        val rooms = listOf(
            createRoom(
                id = 1,
                numberOfRooms = 2,
                numberOfBathrooms = 1,
                city = city,
                type = RoomType.ROOM,
                pricePerMonth = 250000.0,
                leaseType = LeaseType.SHORT_TERM,
                furnishedType = FurnishedType.FULLY_FURNISHED,
            ),
            createRoom(
                id = 2,
                numberOfRooms = 3,
                numberOfBathrooms = 2,
                city = city,
                type = RoomType.ROOM,
                pricePerNight = 50000.0,
                leaseType = LeaseType.SHORT_TERM,
                furnishedType = FurnishedType.FULLY_FURNISHED,
            ),
            createRoom(
                id = 3,
                numberOfRooms = 4,
                numberOfBathrooms = 3,
                city = city,
                type = RoomType.ROOM,
                pricePerNight = 90000.0,
                leaseType = LeaseType.SHORT_TERM,
                furnishedType = FurnishedType.FULLY_FURNISHED,
            ),
        )
        setupRooms(rooms)

        val json = SearchAgent(llm, tool)
            .run("I want a fully furnished room in Yaounde for 3 days. Im ready to pay a maximum of 75000/day")

        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        assertEquals(2, result.properties.size)
        assertEquals(rooms[1].listingUrl, result.properties[0].url)
        assertEquals(rooms[2].listingUrl, result.properties[1].url)
        assertEquals(city.id, result.searchParameters.cityId)
        assertEquals(null, result.searchParameters.neighborhoodId)
        assertEquals("ROOM", result.searchParameters.propertyType)
        assertEquals(null, result.searchParameters.minBedrooms)
        assertEquals(null, result.searchParameters.maxBedrooms)
        assertEquals(null, result.searchParameters.minBudget)
        assertEquals(75000.0, result.searchParameters.maxBudget)
    }

    @Test
    fun empty() {
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        setupRooms(emptyList<RoomSummary>())

        val json = SearchAgent(llm, tool).run("I look for 3 bedrooms apartment in Yaounde")

        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        assertEquals(0, result.properties.size)
    }

    private fun createLocation(id: Long, name: String, type: LocationType): Location {
        return Location(
            id = id,
            name = name,
            type = type,
        )
    }

    private fun createRoom(
        id: Long,
        numberOfRooms: Int,
        numberOfBathrooms: Int,
        city: Location? = null,
        neighborhood: Location? = null,
        type: RoomType = RoomType.APARTMENT,
        pricePerMonth: Double? = null,
        pricePerNight: Double? = null,
        leaseType: LeaseType = LeaseType.SHORT_TERM,
        furnishedType: FurnishedType = FurnishedType.FULLY_FURNISHED,
    ): RoomSummary {
        return RoomSummary(
            id = id,
            listingUrl = "/room/$id",
            numberOfRooms = numberOfRooms,
            numberOfBathrooms = numberOfBathrooms,
            address = Address(
                cityId = city?.id,
            ),
            neighborhoodId = neighborhood?.id,
            type = type,
            pricePerNight = pricePerNight?.let { Money(pricePerNight, "XAF") },
            pricePerMonth = pricePerMonth?.let { Money(pricePerMonth, "XAF") },
            leaseType = leaseType,
            furnishedType = furnishedType,
        )
    }

    private fun setupLocation(location: Location) {
        doReturn(SearchLocationResponse(listOf(location)))
            .whenever(kokiRefData).locations(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    private fun setupLocation(location1: Location, location2: Location) {
        doReturn(SearchLocationResponse(listOf(location1)))
            .doReturn(SearchLocationResponse(listOf(location2)))
            .whenever(kokiRefData).locations(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }

    private fun setupRooms(rooms: List<RoomSummary>) {
        doReturn(SearchRoomResponse(rooms))
            .whenever(kokiRooms).rooms(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )
    }
}
