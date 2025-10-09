package com.wutsi.koki.email.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.email.server.mq.EmailMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class EmailMQConfiguration(
    private val consumer: EmailMQConsumer,
    channel: Channel,
    objectMapper: ObjectMapper,
    publisher: Publisher,

    @Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.module.email.mq.queue}") private val queue: String,
    @Value("\${koki.module.email.mq.dlq}") private val dlq: String,
    @Value("\${koki.module.email.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, objectMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.email.mq.dlq-cron}")
    fun processNotificationDlq() {
        processDlq(queue, dlq)
    }
}
