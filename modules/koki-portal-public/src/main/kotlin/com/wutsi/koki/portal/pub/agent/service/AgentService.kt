package com.wutsi.koki.portal.pub.agent.service

import com.wutsi.koki.portal.pub.agent.mapper.AgentMapper
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.portal.pub.user.service.UserService
import com.wutsi.koki.sdk.KokiAgents
import org.springframework.stereotype.Service

@Service
class AgentService(
    private val koki: KokiAgents,
    private val mapper: AgentMapper,
    private val userService: UserService,
) {
    fun get(
        id: Long,
        fullGraph: Boolean = true,
    ): AgentModel {
        val agent = koki.get(id).agent
        val user = if (fullGraph) {
            userService.get(agent.userId)
        } else {
            UserModel(id = agent.userId)
        }
        return mapper.toAgentModel(agent, user)
    }

    fun search(
        ids: List<Long> = emptyList(),
        userIds: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<AgentModel> {
        val agents = koki.search(
            ids = ids,
            userIds = userIds,
            limit = limit,
            offset = offset,
        ).agents

        val userIds = agents.map { agent -> agent.userId }
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.search(
                ids = userIds,
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        return agents.map { agent ->
            mapper.toAgentModel(
                entity = agent,
                users = users,
            )
        }
    }
}
