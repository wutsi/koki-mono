package com.wutsi.koki.room.server.service.ai

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.refdata.server.domain.LocationEntity
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.server.domain.RoomEntity
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomAgentTest {
    private val llm = mock<LLM>()
    private val amenityService = mock<AmenityService>()
    private val locationService = mock<LocationService>()
    private val accountService = mock<AccountService>()
    private val room = RoomEntity(
        id = 111L,
        type = RoomType.APARTMENT,
        furnishedType = FurnishedType.FULLY_FURNISHED,
        numberOfRooms = 2,
        numberOfBeds = 4,
        numberOfBathrooms = 5,
        maxGuests = 6,
        cityId = 111L,
        neighborhoodId = 333L,
        country = "CA",
    )
    private val agent = RoomAgent(
        llm = llm,
        locationService = locationService,
        accountService = accountService,
        amenityService = amenityService,
        room = room,
    )

    @Test
    fun systemInstructions() {
        assertEquals(
            RoomAgent.SYSTEM_INSTRUCTIONS.trimIndent(),
            agent.systemInstructions()
        )
    }

    @Test
    fun `prompt contains query`() {
        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        assertEquals(true, prompt.contains("Query: This is my query"))
    }

    @Test
    fun `prompt contains observations`() {
        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        assertEquals(true, prompt.contains("Observations:\n- A\n- B"))
    }

    @Test
    fun `prompt contains amenities`() {
        val amenities = listOf(
            AmenityEntity(id = 111, name = "A"),
            AmenityEntity(id = 222, name = "B")
        )
        doReturn(amenities).whenever(amenityService).all()

        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        assertEquals(true, prompt.contains("id,name\n111,A\n222,B"))
    }

    @Test
    fun `prompt contains room info`() {
        doReturn(
            listOf(
                LocationEntity(id = 333L, name = "Bastos"),
                LocationEntity(id = 111L, name = "Yaounde")
            )
        ).whenever(locationService)
            .search(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        val prompt = agent.buildPrompt("This is my query", listOf("A", "B"))
        println(prompt)
        assertEquals(
            true,
            prompt.contains(
                """

            """.trimIndent()
            )
        )
    }
}
