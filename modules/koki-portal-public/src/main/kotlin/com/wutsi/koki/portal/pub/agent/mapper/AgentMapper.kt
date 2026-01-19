package com.wutsi.koki.portal.pub.agent.mapper

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.listing.dto.ListingMetricSummary
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.agent.model.AgentMetricModel
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.common.mapper.MoneyMapper
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class AgentMapper(private val moneyMapper: MoneyMapper) : TenantAwareMapper() {
    fun toAgentModel(
        entity: AgentSummary,
        users: Map<Long, UserModel>,
        metrics: List<ListingMetricSummary>,
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = users[entity.userId] ?: UserModel(id = entity.userId),
            publicUrl = currentTenant.get().clientPortalUrl +
                "/agents/${entity.id}" +
                (users[entity.userId]?.let { user -> "/${user.slug}" } ?: ""),
            activeSaleMetric = toAgentMetricModel(entity.userId, ListingType.SALE, metrics),
            activeRentalMetric = toAgentMetricModel(entity.userId, ListingType.RENTAL, metrics),
        )
    }

    fun toAgentModel(
        entity: Agent,
        user: UserModel,
        metrics: List<ListingMetricSummary>,
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = user,
            publicUrl = "${currentTenant.get().clientPortalUrl}/agents/${entity.id}/${user.slug}",
            activeSaleMetric = toAgentMetricModel(entity.userId, ListingType.SALE, metrics),
            activeRentalMetric = toAgentMetricModel(entity.userId, ListingType.RENTAL, metrics),
        )
    }

    private fun toAgentMetricModel(
        agentUserId: Long,
        listingType: ListingType,
        metrics: List<ListingMetricSummary>,
    ): AgentMetricModel? {
        val total = metrics.filter { metric -> metric.listingType == listingType }
            .filter { metric -> metric.sellerAgentUserId == agentUserId }
            .sumOf { metric -> metric.total }
        if (total <= 0) {
            return null
        }
        val totalPrice = metrics.filter { metric -> metric.listingType == listingType }
            .filter { metric -> metric.sellerAgentUserId == agentUserId }
            .sumOf { metric -> metric.totalPrice }

        val averagePrice = moneyMapper.toMoneyModel(totalPrice / total, metrics[0].currency)

        return AgentMetricModel(total, averagePrice)
    }
}
