package com.wutsi.koki.agent.server.mapper

import com.wutsi.koki.agent.dto.Agent
import com.wutsi.koki.agent.dto.AgentMetric
import com.wutsi.koki.agent.dto.AgentSummary
import com.wutsi.koki.agent.server.domain.AgentEntity
import com.wutsi.koki.agent.server.domain.AgentMetricEntity
import org.springframework.stereotype.Service

@Service
class AgentMapper {
    fun toAgent(entity: AgentEntity): Agent {
        return Agent(
            id = entity.id ?: -1,
            userId = entity.userId,
            totalSales = entity.totalSales,
            totalRentals = entity.totalRentals,
            past12mSales = entity.past12mSales,
            past12mRentals = entity.past12mRentals,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            metrics = entity.metrics.map { metric -> toAgentMetric(metric) }
        )
    }

    fun toAgentSummary(entity: AgentEntity): AgentSummary {
        return AgentSummary(
            id = entity.id ?: -1,
            userId = entity.userId,
            totalSales = entity.totalSales,
            totalRentals = entity.totalRentals,
            past12mSales = entity.past12mSales,
            past12mRentals = entity.past12mRentals,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }

    fun toAgentMetric(entity: AgentMetricEntity): AgentMetric {
        return AgentMetric(
            id = entity.id ?: -1,
            listingType = entity.listingType,
            period = entity.period,
            total = entity.total,
            minPrice = entity.minPrice,
            maxPrice = entity.maxPrice,
            averagePrice = entity.averagePrice,
            totalPrice = entity.totalPrice,
            currency = entity.currency,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
