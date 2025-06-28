package com.wutsi.koki.chatbot.ai.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.ai.llm.Type
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchRoomToolTest {
    private val kokiRooms = mock<KokiRooms>()
    private val kokiRefData = mock<KokiRefData>()
    private val objectMapper = ObjectMapper()

    private val tool = SearchRoomTool(
        kokiRooms = kokiRooms,
        kokiRefData = kokiRefData,
        objectMapper = objectMapper,
    )

    @Test
    fun function() {
        assertEquals(SearchRoomTool.NAME, tool.function().name)
        assertEquals(SearchRoomTool.DESCRIPTION, tool.function().description)
        assertEquals(Type.OBJECT, tool.function().parameters?.type)
        assertEquals(10, tool.function().parameters?.properties?.size)
    }

    @Test
    fun `nothing found`() {
        setupRooms(emptyList())

        val result = tool.use(
            mapOf(
                "propertyType" to RoomType.APARTMENT
            )
        )

        assertEquals(SearchRoomTool.MESSAGE_NOT_FOUND, result)
    }

    @Test
    fun `search in city`() {
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        val rooms = listOf(
            createRoom(id = 1, numberOfRooms = 2, numberOfBathrooms = 1),
            createRoom(id = 2, numberOfRooms = 3, numberOfBathrooms = 2),
        )
        setupRooms(rooms)

        val result = tool.use(
            mapOf(
                "country" to "CM",
                "city" to city.name,
                "minBedrooms" to 2,
                "maxBedrooms" to 4,
                "minBudget" to 100,
                "maxBudget" to 1000,
                "leaseType" to LeaseType.SHORT_TERM.name,
                "furnishedType" to FurnishedType.FULLY_FURNISHED.name,
            )
        )

        verify(kokiRooms).rooms(
            emptyList(), // ids
            city.id, // cityId
            null, // neighborhoodId
            RoomStatus.PUBLISHED, // status
            null, // totalGuest
            emptyList(), // types
            emptyList(), // amenityIds
            2, // minRooms
            4, // maxRooms
            null, // minBathrooms
            null, // maxBathrooms
            emptyList(), // categoryIds
            emptyList(), // accountIds
            emptyList(), // accountManagerIds
            null,
            null,
            null,
            null,
            SearchRoomTool.MAX_RECOMMENDATIONS, // limit
            0, // offset
        )

        assertEquals(true, result.contains(SearchRoomTool.MESSAGE_FOUND))
//        assertEquals(true, result.contains(objectMapper.writeValueAsString(rooms)))
    }

    @Test
    fun `search in neighborhood`() {
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        val neighborhood = createLocation(id = 12L, name = "Bastos", type = LocationType.NEIGHBORHOOD)
        setupLocation(city, neighborhood)

        setupRooms(
            listOf(
                createRoom(id = 1, numberOfRooms = 2, numberOfBathrooms = 1),
                createRoom(id = 2, numberOfRooms = 3, numberOfBathrooms = 2),
            )
        )

        val result = tool.use(
            mapOf(
                "country" to "CM",
                "city" to city.name,
                "neighborhood" to neighborhood.name,
                "propertyType" to RoomType.APARTMENT,
                "minBedrooms" to 2,
                "maxBedrooms" to 4,
            )
        )

        assertEquals(true, result.contains(SearchRoomTool.MESSAGE_FOUND))
        verify(kokiRooms).rooms(
            emptyList(), // ids
            city.id, // cityId
            neighborhood.id, // neighborhoodId
            RoomStatus.PUBLISHED, // status
            null, // totalGuest
            listOf(RoomType.APARTMENT), // types
            emptyList(), // amenityIds
            2, // minRooms
            4, // maxRooms
            null, // minBathrooms
            null, // maxBathrooms
            emptyList(), // categoryIds
            emptyList(), // accountIds
            emptyList(), // accountManagerIds
            null,
            null,
            null,
            null,
            SearchRoomTool.MAX_RECOMMENDATIONS, // limit
            0, // offset
        )
    }

    private fun createLocation(id: Long, name: String, type: LocationType): Location {
        return Location(
            id = id,
            name = name,
            type = type,
        )
    }

    private fun createRoom(id: Long, numberOfRooms: Int, numberOfBathrooms: Int): RoomSummary {
        return RoomSummary(
            id = id,
            listingUrl = "/room/$id",
            numberOfRooms = numberOfRooms,
            numberOfBathrooms = numberOfBathrooms,
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
