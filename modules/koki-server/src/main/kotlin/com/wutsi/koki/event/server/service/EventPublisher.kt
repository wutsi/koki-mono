package com.wutsi.koki.event.server.service

interface EventPublisher {
    fun publish(event: Any)
}
