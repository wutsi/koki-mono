package com.wutsi.koki.portal.pub.agent.service

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.pub.agent.mapper.AgentMapper
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.user.service.UserService
import com.wutsi.koki.sdk.KokiAgents
import com.wutsi.koki.sdk.KokiListings
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AgentService(
    private val koki: KokiAgents,
    private val mapper: AgentMapper,
    private val userService: UserService,
    private val kokiListings: KokiListings,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AgentService::class.java)
    }

    fun get(
        id: Long,
        fullGraph: Boolean = true,
    ): AgentModel {
        val agent = koki.get(id).agent
        val user = userService.get(agent.userId)

        val metrics = if (fullGraph) {
            try {
                kokiListings.metrics(
                    sellerAgentUserIds = listOf(user.id),
                    listingStatus = ListingStatus.ACTIVE,
                    dimension = ListingMetricDimension.SELLER_AGENT,
                ).metrics
            } catch (e: Throwable) {
                LOGGER.warn("Unable to fetch metrics for agent-id: $id", e)
                emptyList()
            }
        } else {
            emptyList()
        }

        return mapper.toAgentModel(
            entity = agent,
            user = user,
            metrics = metrics
        )
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
        val users = if (userIds.isEmpty()) {
            emptyMap()
        } else {
            userService.search(
                ids = userIds,
                limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val metrics = if (userIds.isEmpty() || !fullGraph) {
            emptyList()
        } else {
            try {
                kokiListings.metrics(
                    sellerAgentUserIds = userIds,
                    listingStatus = ListingStatus.ACTIVE,
                    dimension = ListingMetricDimension.SELLER_AGENT,
                ).metrics
            } catch (e: Throwable) {
                LOGGER.warn("Unable to fetch metrics for agent-user-ids: ${userIds.joinToString(",")}", e)
                emptyList()
            }
        }

        return agents.map { agent ->
            mapper.toAgentModel(
                entity = agent,
                users = users,
                metrics = metrics,
            )
        }
    }
}
