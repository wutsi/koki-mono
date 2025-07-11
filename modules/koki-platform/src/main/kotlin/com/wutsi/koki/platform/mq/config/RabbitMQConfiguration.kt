package com.wutsi.koki.platform.mq.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.mq.rabbitmq.RabbitMQHealthIndicator
import com.wutsi.koki.platform.mq.rabbitmq.RabbitMQPublisher
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.mq.type"],
    havingValue = "rabbitmq",
    matchIfMissing = false,
)
class RabbitMQConfiguration(
    private val executorService: ExecutorService,
    private val objectMapper: ObjectMapper,
    private val storageServiceBuilder: StorageServiceBuilder,

    @Value("\${wutsi.platform.mq.rabbitmq.url}") private val url: String,
    @Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${wutsi.platform.mq.rabbitmq.max-retries:24}") private val maxRetries: Int,
    @Value("\${wutsi.platform.mq.rabbitmq.ttl-seconds:86400}") private val ttl: Int
) {
    @Bean
    open fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.setUri(url)
        return factory
    }

    @Bean(destroyMethod = "close")
    open fun channel(): Channel {
        val result = connectionFactory()
            .newConnection(executorService)
            .createChannel()
        return result
    }

    @Bean
    fun publisher(): Publisher {
        return RabbitMQPublisher(
            channel = channel(),
            exchangeName = exchangeName,
            ttl = ttl,
            maxRetries = maxRetries,
            objectMapper = objectMapper,
            storageBuilder = storageServiceBuilder,
        )
    }

    @Bean
    fun rabbitMQHealthIndicator(): HealthIndicator {
        return RabbitMQHealthIndicator(channel())
    }
}
