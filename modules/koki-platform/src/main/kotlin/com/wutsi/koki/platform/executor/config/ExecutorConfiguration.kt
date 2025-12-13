package com.wutsi.koki.platform.executor.config

import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorConfiguration(
    @param:Value("\${wutsi.platform.executor.thread-pool.name}") private val name: String,
    @param:Value("\${wutsi.platform.executor.thread-pool.size:16}") private val size: Int
) {
    @Bean(destroyMethod = "shutdown")
    open fun executorService(): ExecutorService {
        val tf = BasicThreadFactory
            .builder()
            .namingPattern("$name-%d")
            .build()

        return Executors.newFixedThreadPool(size, tf)
    }
}
