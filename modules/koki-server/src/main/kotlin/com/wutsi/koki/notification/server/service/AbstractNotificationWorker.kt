package com.wutsi.koki.notification.server.service

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

abstract class AbstractNotificationWorker(
    private val registry: NotificationMQConsumer
) : NotificationWorker {
    @PostConstruct
    fun setUp() {
        registry.register(this)
    }

    @PreDestroy
    fun tearDown() {
        registry.unregister(this)
    }
}
