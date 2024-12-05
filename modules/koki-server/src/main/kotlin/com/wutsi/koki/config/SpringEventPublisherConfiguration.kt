package com.wutsi.koki.config

import com.wutsi.koki.event.server.service.DefaultEventPublisher
import com.wutsi.koki.event.server.service.EventPublisher
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import java.util.concurrent.ExecutorService

@Configuration
@ConditionalOnProperty(
    value = ["koki.event-publisher.type"],
    havingValue = "spring",
)
class SpringEventPublisherConfiguration(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val executorService: ExecutorService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SpringEventPublisherConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info("EventPublisher configured")
    }

    @Bean
    fun eventPublisher(): EventPublisher {
        return DefaultEventPublisher(applicationEventPublisher)
    }

    @Bean
    fun applicationEventMulticaster(): ApplicationEventMulticaster {
        val multicaster = SimpleApplicationEventMulticaster()
        multicaster.setTaskExecutor(executorService)
        return multicaster
    }
}
