package com.wutsi.koki.portal.agent.mapper

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.portal.agent.model.AgentModel
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class AgentMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toAgentModel(
        entity: AgentSummary,
        users: Map<Long, UserModel>,
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = users[entity.userId] ?: UserModel(id = entity.userId),
        )
    }

    fun toAgentModel(
        entity: Agent,
        user: UserModel,
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = user,
        )
    }
}
