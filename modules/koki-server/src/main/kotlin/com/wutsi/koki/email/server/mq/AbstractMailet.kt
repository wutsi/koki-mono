package com.wutsi.koki.email.server.mq

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractMailet : Mailet {
    @Autowired
    protected lateinit var registry: EmailMQConsumer

    @PostConstruct
    fun init() {
        registry.register(this)
    }

    @PreDestroy
    fun destroy() {
        registry.unregister(this)
    }
}
