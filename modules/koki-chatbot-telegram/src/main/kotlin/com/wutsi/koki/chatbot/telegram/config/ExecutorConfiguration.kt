package com.wutsi.koki.chatbot.telegram.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorConfiguration(
    @Value("\${koki.executor.thread-pool-size}") private val size: Int
) {
    @Bean(destroyMethod = "shutdown")
    open fun executorService(): ExecutorService {
        return Executors.newFixedThreadPool(size)
    }
}
