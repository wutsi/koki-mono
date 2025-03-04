package com.wutsi.koki.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.mq.rabbitmq.RabbitMQPublisher
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService

@Configuration
class RabbitMQConfiguration(
    private val executorService: ExecutorService,
    private val objectMapper: ObjectMapper,
    private val storageServiceBuilder: StorageServiceBuilder,

    @Value("\${koki.rabbitmq.url}") private val url: String,
    @Value("\${koki.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.rabbitmq.max-retries}") private val maxRetries: Int,
    @Value("\${koki.rabbitmq.ttl-seconds}") private val ttl: Int
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info("Setting up exchange: $exchangeName")
        channel().exchangeDeclare(
            exchangeName,
            BuiltinExchangeType.FANOUT,
            true, // durable
        )
        LOGGER.info("EventPublisher configured")
    }

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
}
