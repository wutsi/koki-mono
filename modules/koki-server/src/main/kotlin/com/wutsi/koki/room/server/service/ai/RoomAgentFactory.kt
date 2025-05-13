package com.wutsi.koki.room.server.service.ai

import com.wutsi.koki.ai.server.service.LLMProvider
import org.springframework.stereotype.Service

@Service
class RoomAgentFactory (
    private val llmProvider: LLMProvider,
){
    fun createRoomImageAgent(tenantId: Long): RoomImageAgent {
        val llm = llmProvider.get(tenantId)
        return RoomImageAgent(llm = llm)
    }
}
