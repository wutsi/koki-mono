package com.wutsi.koki.config

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService

@Configuration
class RabbitMQConfiguration(
    private val executorService: ExecutorService,

    @Value("\${koki.rabbitmq.url}") private val url: String,
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
}
