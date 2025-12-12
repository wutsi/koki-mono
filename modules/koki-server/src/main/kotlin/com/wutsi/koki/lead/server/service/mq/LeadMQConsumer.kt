package com.wutsi.koki.lead.server.service.mq

import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.platform.mq.Consumer

class LeadMQConsumer(
    private val leadMessageReceivedEventHandler: LeadMessageReceivedEventHandler,
) : Consumer {
    override fun consume(event: Any): Boolean {
        return if (event is LeadMessageReceivedEvent) {
            leadMessageReceivedEventHandler.handle(event)
        } else {
            false
        }
    }
}
