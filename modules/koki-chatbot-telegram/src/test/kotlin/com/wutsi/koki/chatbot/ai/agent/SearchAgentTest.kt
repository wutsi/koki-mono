package com.wutsi.koki.chatbot.ai.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.ai.tool.SearchRoomTool
import com.wutsi.koki.platform.ai.llm.deepseek.Deepseek
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.RoomSummary
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
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        val rooms = listOf(
            createRoom(id = 1, numberOfRooms = 2, numberOfBathrooms = 1),
            createRoom(id = 2, numberOfRooms = 3, numberOfBathrooms = 2),
            createRoom(id = 3, numberOfRooms = 4, numberOfBathrooms = 3),
        )
        setupRooms(rooms)

        val json = SearchAgent(llm, tool).run("I look for apartment in Yaounde")

        val result = objectMapper.readValue(json, SearchAgentData::class.java)
        assertEquals(rooms.size, result.properties.size)
        assertEquals(rooms[0].listingUrl, result.properties[0].url)
        assertEquals(rooms[1].listingUrl, result.properties[1].url)
        assertEquals(rooms[2].listingUrl, result.properties[2].url)
    }

    @Test
    fun empty() {
        val city = createLocation(id = 11L, name = "Yaounde", type = LocationType.CITY)
        setupLocation(city)

        setupRooms(emptyList<RoomSummary>())

        val json = SearchAgent(llm, tool).run("I look for apartment in Yaounde")

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
            )
    }
}
