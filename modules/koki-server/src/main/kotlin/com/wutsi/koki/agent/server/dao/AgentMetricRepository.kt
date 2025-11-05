package com.wutsi.koki.agent.server.dao

import com.wutsi.koki.agent.dto.MetricPeriod
import com.wutsi.koki.agent.server.domain.AgentMetricEntity
import com.wutsi.koki.listing.dto.ListingType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AgentMetricRepository : CrudRepository<AgentMetricEntity, Long> {
    fun findByAgentIdAndListingTypeAndPeriod(
        agentId: Long,
        listingType: ListingType,
        period: MetricPeriod
    ): AgentMetricEntity?
}
