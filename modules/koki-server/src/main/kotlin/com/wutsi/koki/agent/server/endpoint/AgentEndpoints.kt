package com.wutsi.koki.agent.server.endpoint

import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.agent.dto.SearchAgentResponse
import com.wutsi.koki.agent.server.job.AgentMetricJobs
import com.wutsi.koki.agent.server.mapper.AgentMapper
import com.wutsi.koki.agent.server.service.AgentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/agents")
class AgentEndpoints(
    private val service: AgentService,
    private val mapper: AgentMapper,
    private val jobs: AgentMetricJobs
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAgentResponse {
        val agent = service.get(id, tenantId)
        return GetAgentResponse(
            agent = mapper.toAgent(agent)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "user-id") userIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "limit") limit: Int = 20,
        @RequestParam(required = false, name = "offset") offset: Int = 0,
    ): SearchAgentResponse {
        val agents = service.search(
            tenantId = tenantId,
            ids = ids,
            userIds = userIds,
            limit = limit,
            offset = offset,
        )
        return SearchAgentResponse(
            agents = agents.map { agent -> mapper.toAgentSummary(agent) }
        )
    }

    @GetMapping("/jobs/metrics/daily")
    fun jobDailyMetrics() {
        jobs.daily()
    }

    @GetMapping("/jobs/metrics/monthly")
    fun jobMonthlyMetrics() {
        jobs.monthly()
    }
}
