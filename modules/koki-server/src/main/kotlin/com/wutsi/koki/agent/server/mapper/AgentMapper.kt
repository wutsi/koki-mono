package com.wutsi.koki.agent.server.mapper

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.agent.server.domain.AgentEntity
import org.springframework.stereotype.Service

@Service
class AgentMapper {
    fun toAgent(entity: AgentEntity): Agent {
        return Agent(
            id = entity.id ?: -1,
            userId = entity.userId,
            qrCodeUrl = entity.qrCodeUrl,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toAgentSummary(entity: AgentEntity): AgentSummary {
        return AgentSummary(
            id = entity.id ?: -1,
            userId = entity.userId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
