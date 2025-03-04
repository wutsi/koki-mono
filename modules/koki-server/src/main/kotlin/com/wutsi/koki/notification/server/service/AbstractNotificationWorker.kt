package com.wutsi.koki.notification.server.mq

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractNotificationWorker: NotificationWorker {
    @Autowired
    private lateinit var registry: NotificationConsumer

    @PostConstruct
    fun setUp(){
        registry.register(this)
    }

    @PreDestroy
    fun tearDown(){
        registry.unregister(this)
    }
}
