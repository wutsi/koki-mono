package com.wutsi.koki.agent.server.service.mq

import com.wutsi.koki.agent.dto.event.AgentCreatedEvent
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import org.springframework.stereotype.Service

@Service
class AgentMQConsumer(
    private val userCreatedEventHandler: AgentUserCreatedEventHandler,
    private val agentCreatedEventHandler: AgentCreatedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is UserCreatedEvent) {
            userCreatedEventHandler.handle(event)
        } else if (event is AgentCreatedEvent) {
            agentCreatedEventHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
