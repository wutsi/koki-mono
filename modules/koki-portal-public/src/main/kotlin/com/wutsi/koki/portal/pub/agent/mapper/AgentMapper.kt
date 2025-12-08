package com.wutsi.koki.portal.pub.agent.mapper

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentMetric
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.agent.dto.MetricPeriod
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
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = users[entity.userId] ?: UserModel(id = entity.userId),
            totalSales = entity.totalSales,
            totalRentals = entity.totalRentals,
            past12mSales = entity.past12mSales,
            past12mRentals = entity.past12mRentals,
        )
    }

    fun toAgentModel(
        entity: Agent,
        user: UserModel,
    ): AgentModel {
        return AgentModel(
            id = entity.id,
            user = user,
            totalSales = entity.totalSales,
            totalRentals = entity.totalRentals,
            past12mSales = entity.past12mSales,
            past12mRentals = entity.past12mRentals,

            totalSalesMetric = entity.metrics
                .find { metric -> metric.period == MetricPeriod.OVERALL && metric.listingType == ListingType.SALE }
                ?.let { metric -> toAgentMetricModel(metric) },

            past12mSalesMetric = entity.metrics
                .find { metric -> metric.period == MetricPeriod.PAST_12M && metric.listingType == ListingType.SALE }
                ?.let { metric -> toAgentMetricModel(metric) },

            totalRentalsMetric = entity.metrics
                .find { metric -> metric.period == MetricPeriod.OVERALL && metric.listingType == ListingType.RENTAL }
                ?.let { metric -> toAgentMetricModel(metric) },

            past12mRentalsMetric = entity.metrics
                .find { metric -> metric.period == MetricPeriod.PAST_12M && metric.listingType == ListingType.RENTAL }
                ?.let { metric -> toAgentMetricModel(metric) },
        )
    }

    fun toAgentMetricModel(entity: AgentMetric): AgentMetricModel {
        return AgentMetricModel(
            total = entity.total,
            minPrice = moneyMapper.toMoneyModel(entity.minPrice, entity.currency),
            maxPrice = moneyMapper.toMoneyModel(entity.maxPrice, entity.currency),
            averagePrice = moneyMapper.toMoneyModel(entity.averagePrice, entity.currency),
            totalPrice = moneyMapper.toMoneyModel(entity.totalPrice, entity.currency),
        )
    }
}
