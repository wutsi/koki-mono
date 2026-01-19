package com.wutsi.koki.agent.server.service.mq

import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import org.springframework.stereotype.Service

@Service
class AgentMQConsumer(
    private val userCreatedEventHandler: AgentUserCreatedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is UserCreatedEvent) {
            userCreatedEventHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
