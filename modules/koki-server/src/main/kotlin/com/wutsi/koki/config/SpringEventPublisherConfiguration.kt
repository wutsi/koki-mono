package com.wutsi.koki.config

import com.wutsi.koki.event.server.service.DefaultEventPublisher
import com.wutsi.koki.event.server.service.EventPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import java.util.concurrent.Executors

@Configuration
class SpringEventPublisherConfiguration(
    private val applicationEventPublisher: ApplicationEventPublisher,

    @Value("\${koki.event-publisher.spring.thread-pool-size}") private val size: Int
) {
    @Bean
    fun eventPublisher(): EventPublisher {
        return DefaultEventPublisher(applicationEventPublisher)
    }

    @Bean
    fun applicationEventMulticaster(): ApplicationEventMulticaster {
        val executor = Executors.newFixedThreadPool(size)
        val multicaster = SimpleApplicationEventMulticaster()
        multicaster.setTaskExecutor(executor)
        return multicaster
    }
}
