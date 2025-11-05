package com.wutsi.koki.agent.server.mq

import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import org.springframework.stereotype.Service

@Service
class AgentMQConsumer(
    private val userCreatedEventHandler: AgentUserCreatedEventHandler,
    private val listingStatusChangedEventHandler: AgentListingStatusChangedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is UserCreatedEvent) {
            userCreatedEventHandler.handle(event)
        } else if (event is ListingStatusChangedEvent) {
            listingStatusChangedEventHandler.handle(event)
        } else {
            return false
        }
        return true
    }
}
