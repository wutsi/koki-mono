package com.wutsi.koki.agent.server.service.mq

import com.wutsi.koki.agent.dto.event.AgentCreatedEvent
import com.wutsi.koki.agent.server.service.AgentService
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class AgentCreatedEventHandler(
    private val agentService: AgentService,
    private val logger: KVLogger,
) {
    fun handle(event: AgentCreatedEvent) {
        logger.add("event_agent_id", event.agentId)
        logger.add("event_tenant_id", event.tenantId)

        agentService.generateQrCode(event.agentId, event.tenantId)
    }
}
