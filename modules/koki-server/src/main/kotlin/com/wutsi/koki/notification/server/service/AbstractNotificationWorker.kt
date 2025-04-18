package com.wutsi.koki.notification.server.service

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

abstract class AbstractNotificationWorker(
    private val registry: NotificationMQConsumer
) : NotificationWorker {
    @PostConstruct
    fun init() {
        registry.register(this)
    }

    @PreDestroy
    fun destroy() {
        registry.unregister(this)
    }
}
