package com.wutsi.koki.notification.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class NotificationRabbitMQConsumerConfiguration(
    private val invoiceConsumer: NotificationConsumer,
    channel: Channel,
    objectMapper: ObjectMapper,
    publisher: Publisher,

    @Value("\${koki.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.module.notification.mq.queue}") private val queue: String,
    @Value("\${koki.module.notification.mq.dlq}") private val dlq: String,
    @Value("\${koki.module.notification.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, objectMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, invoiceConsumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.notification.mq.dlq-cron}")
    fun processNotificationDlq() {
        processDlq(queue, dlq)
    }
}
