package com.wutsi.koki.event.server.service

import org.springframework.context.ApplicationEventPublisher

class DefaultEventPublisher(private val delegate: ApplicationEventPublisher) : EventPublisher {
    override fun publish(event: Any) {
        delegate.publishEvent(event)
    }
}
