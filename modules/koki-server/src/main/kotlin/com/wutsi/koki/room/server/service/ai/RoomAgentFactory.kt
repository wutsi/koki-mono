package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.refdata.server.service.AmenityService
import com.wutsi.koki.refdata.server.service.LocationService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.springframework.stereotype.Service

@Service
class RoomAgentFactory(
    private val llmProvider: LLMProvider,
    private val amenityService: AmenityService,
    private val locationService: LocationService,
    private val accountService: AccountService,
    private val configurationService: ConfigurationService,
) {
    fun createRoomImageAgent(tenantId: Long): RoomImageAgent? {
        if (!isAIEnabled(tenantId)) {
            return null
        }

        val llm = llmProvider.get(tenantId)
        return RoomImageAgent(llm = llm)
    }

    fun createRoomInformationFactory(room: RoomEntity): RoomInformationAgent? {
        if (!isAIEnabled(room.tenantId)) {
            return null
        }

        val llm = llmProvider.get(room.tenantId)
        return RoomInformationAgent(
            llm = llm,
            room = room,
            amenityService = amenityService,
            locationService = locationService,
            accountService = accountService,
        )
    }

    private fun isAIEnabled(tenantId: Long): Boolean {
        val configs = configurationService.search(
            tenantId = tenantId, names = listOf(
                ConfigurationName.AI_PROVIDER,
            )
        )
        return configs.size >= 1
    }
}
