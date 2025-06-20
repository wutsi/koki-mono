package com.wutsi.koki.room.server.service.ai

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import kotlin.test.Test

class RoomAgentFactoryTest {
    private val llmProvider = mock<LLMProvider>()
    private val amenityService = mock<AmenityService>()
    private val locationService = mock<LocationService>()
    private val accountService = mock<AccountService>()
    private val configurationService = mock<ConfigurationService>()
    private val llm = mock<LLM>()

    private val factory = RoomAgentFactory(
        llmProvider = llmProvider,
        amenityService = amenityService,
        locationService = locationService,
        accountService = accountService,
        configurationService = configurationService,
    )

    @BeforeEach
    fun setUp() {
        doReturn(listOf(ConfigurationEntity(name = ConfigurationName.AI_PROVIDER, value = "GEMINI")))
            .whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())

        doReturn(llm).whenever(llmProvider).get(any())
    }

    @Test
    fun `image agent`() {
        val agent = factory.createRoomImageAgent(11)
        assertNotNull(agent is ImageAgent)
    }

    @Test
    fun `image agent - no AI`() {
        disableAI()

        val agent = factory.createRoomImageAgent(11)
        assertNull(agent)
    }

    @Test
    fun `room agent`() {
        val agent = factory.createRoomInformationFactory(RoomEntity())
        assertNotNull(agent)
    }

    @Test
    fun `room agent - no ai`() {
        disableAI()

        val agent = factory.createRoomInformationFactory(RoomEntity())
        assertNull(agent)
    }

    fun disableAI() {
        doReturn(emptyList<ConfigurationEntity>())
            .whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())
    }
}
