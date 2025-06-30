package com.wutsi.koki.chatbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.agent.SearchParameterAgent
import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.dto.SearchRoomResponse
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRooms
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ChatbotTest {
    private val kokiRefData = mock<KokiRefData>()
    private val kokiRooms = mock<KokiRooms>()
    private val agentFactory = mock<AgentFactory>()
    private val objectMapper = ObjectMapper()
    private val maxRecommendation = 3
    private val chatbot = Chatbot(
        kokiRefData = kokiRefData,
        kokiRooms = kokiRooms,
        agentFactory = agentFactory,
        objectMapper = objectMapper,
        maxRecommendation = maxRecommendation
    )

    private val agent = mock<SearchParameterAgent>()
    private val city = Location(id = 11, name = "Yaounde")
    private val neighborhood = Location(id = 22, name = "Bastos")

    @BeforeEach
    fun setUp() {
        doReturn(agent).whenever(agentFactory).createSearchParameterAgent()
    }

    @Test
    fun `invalid response`() {
        // GIVEN
        val params = SearchParameters(
            valid = false,
            invalidReason = "Invalid",
        )
        doReturn(
            objectMapper.writeValueAsString(params)
        ).whenever(agent).run(any())

        // WHEN
        assertThrows<InvalidQueryException> {
            chatbot.process(
                ChatbotRequest(
                    query = "Yo",
                    language = "fr",
                    country = "CM",
                )
            )
        }
    }

    @Test
    fun `not found`() {
        // GIVEN
        val params = SearchParameters(
            valid = true,
            invalidReason = null,
            city = "Yaounde",
            neighborhood = "Bastos",
            leaseType = LeaseType.SHORT_TERM.name,
            furnishedType = FurnishedType.FULLY_FURNISHED.name,
            maxBudget = 1000.0,
            minBudget = 50.0,
            maxBedrooms = 3,
            minBedrooms = 1,
            propertyType = RoomType.APARTMENT.name,
        )
        doReturn(
            objectMapper.writeValueAsString(params)
        ).whenever(agent).run(any())

        setupLocation(city, neighborhood)
        setupRooms(emptyList<RoomSummary>())

        // WHEN
        val response = chatbot.process(
            ChatbotRequest(
                query = "Yo",
                language = "fr",
                country = "CM",
            )
        )

        // THEN
        assertEquals(true, response.rooms.isEmpty())
        assertEquals(neighborhood, response.searchLocation)
        assertEquals(params, response.searchParameters)

        verify(kokiRooms).rooms(
            emptyList(), // ids
            city.id, // cityId
            neighborhood.id, // neighborhoodId
            RoomStatus.PUBLISHED, // status
            null, // totalGuest
            listOf(RoomType.valueOf(params.propertyType!!)), // types
            emptyList(), // amenityIds
            params.minBedrooms, // minRooms
            params.maxBedrooms, // maxRooms
            null, // minBathrooms
            null, // maxBathrooms
            emptyList(), // categoryIds
            emptyList(), // accountIds
            emptyList(), // accountManagerIds
            params.minBudget, // min-budget
            params.maxBudget, // max-budget
            LeaseType.valueOf(params.leaseType!!), // lease-type
            FurnishedType.valueOf(params.furnishedType!!), // furnished-type
            maxRecommendation, // limit
            0, // offset
        )
    }

    @Test
    fun `rooms found`() {
        // GIVEN
        val params = SearchParameters(
            valid = true,
            invalidReason = null,
            city = "Yaounde",
            neighborhood = null,
            leaseType = LeaseType.SHORT_TERM.name,
            furnishedType = FurnishedType.FULLY_FURNISHED.name,
            maxBudget = 1000.0,
            minBudget = 50.0,
            maxBedrooms = 3,
            minBedrooms = 1,
            propertyType = RoomType.APARTMENT.name,
        )
        doReturn(
            objectMapper.writeValueAsString(params)
        ).whenever(agent).run(any())

        setupLocation(city)

        val rooms = listOf(
            RoomSummary(id = 1),
            RoomSummary(id = 2),
            RoomSummary(id = 3)
        )
        setupRooms(rooms)

        // WHEN
        val response = chatbot.process(
            ChatbotRequest(
                query = "Yo",
                language = "fr",
                country = "CM",
            )
        )

        // THEN
        assertEquals(rooms, response.rooms)
        assertEquals(city, response.searchLocation)
        assertEquals(params, response.searchParameters)

        verify(kokiRooms).rooms(
            emptyList(), // ids
            city.id, // cityId
            null, // neighborhoodId
            RoomStatus.PUBLISHED, // status
            null, // totalGuest
            listOf(RoomType.valueOf(params.propertyType!!)), // types
            emptyList(), // amenityIds
            params.minBedrooms, // minRooms
            params.maxBedrooms, // maxRooms
            null, // minBathrooms
            null, // maxBathrooms
            emptyList(), // categoryIds
            emptyList(), // accountIds
            emptyList(), // accountManagerIds
            params.minBudget, // min-budget
            params.maxBudget, // max-budget
            LeaseType.valueOf(params.leaseType!!), // lease-type
            FurnishedType.valueOf(params.furnishedType!!), // furnished-type
            maxRecommendation, // limit
            0, // offset
        )
    }

    private fun setupLocation(city: Location) {
        doReturn(SearchLocationResponse(listOf(city)))
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

    private fun setupLocation(city: Location, neighborhood: Location) {
        doReturn(SearchLocationResponse(listOf(city)))
            .doReturn(SearchLocationResponse(listOf(neighborhood)))
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
