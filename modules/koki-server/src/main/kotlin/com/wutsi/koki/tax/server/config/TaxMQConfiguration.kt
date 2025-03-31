package com.wutsi.koki.tax.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tax.server.service.TaxMQConsumer
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class TaxMQConfiguration(
    private val consumer: TaxMQConsumer,
    channel: Channel,
    objectMapper: ObjectMapper,
    publisher: Publisher,

    @Value("\${koki.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.module.tax.mq.queue}") private val queue: String,
    @Value("\${koki.module.tax.mq.dlq}") private val dlq: String,
    @Value("\${koki.module.tax.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, objectMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.tax.mq.dlq-cron}")
    fun processTaxDlq() {
        processDlq(queue, dlq)
    }
}
