package com.wutsi.koki.notification.server.service

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

abstract class AbstractNotificationWorker(
    private val registry: NotificationConsumer
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
