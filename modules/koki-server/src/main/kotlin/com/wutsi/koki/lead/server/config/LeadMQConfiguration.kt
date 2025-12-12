package com.wutsi.koki.lead.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.lead.server.service.mq.LeadMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class LeadMQConfiguration(
    private val consumer: LeadMQConsumer,
    channel: Channel,
    objectMapper: ObjectMapper,
    publisher: Publisher,

    @Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.module.lead.mq.queue}") private val queue: String,
    @Value("\${koki.module.lead.mq.dlq}") private val dlq: String,
    @Value("\${koki.module.lead.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, objectMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.lead.mq.dlq-cron}")
    fun processRoomDlq() {
        processDlq(queue, dlq)
    }
}
