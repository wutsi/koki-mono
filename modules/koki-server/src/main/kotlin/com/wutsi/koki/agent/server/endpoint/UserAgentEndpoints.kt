package com.wutsi.koki.agent.server.endpoint

import com.wutsi.koki.agent.dto.GetAgentResponse
import com.wutsi.koki.agent.server.mapper.AgentMapper
import com.wutsi.koki.agent.server.service.AgentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAgentEndpoints(
    private val service: AgentService,
    private val mapper: AgentMapper,
) {
    @GetMapping("/v1/users/{id}/agent")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetAgentResponse {
        val agent = service.getByUser(id, tenantId)
        return GetAgentResponse(
            agent = mapper.toAgent(agent)
        )
    }
}
