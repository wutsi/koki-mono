package com.wutsi.koki.lead.server.config

import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.lead.server.service.mq.LeadMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import tools.jackson.databind.json.JsonMapper

@Configuration
class LeadMQConfiguration(
    private val consumer: LeadMQConsumer,
    channel: Channel,
    jsonMapper: JsonMapper,
    publisher: Publisher,

    @param:Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @param:Value("\${koki.module.lead.mq.queue}") private val queue: String,
    @param:Value("\${koki.module.lead.mq.dlq}") private val dlq: String,
    @param:Value("\${koki.module.lead.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, jsonMapper, publisher) {
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
