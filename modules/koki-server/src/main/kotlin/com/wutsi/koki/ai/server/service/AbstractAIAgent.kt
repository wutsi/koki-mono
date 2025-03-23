package com.wutsi.koki.ai.server.service

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

abstract class AbstractAIAgent(
    private val registry: AIConsumer
): AIAgent {
    @PostConstruct
    fun setUp() {
        registry.register(this)
    }

    @PreDestroy
    fun tearDown() {
        registry.unregister(this)
    }
}
