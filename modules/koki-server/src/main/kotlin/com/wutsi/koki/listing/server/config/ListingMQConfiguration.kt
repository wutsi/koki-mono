package com.wutsi.koki.listing.server.config

import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.listing.server.service.mq.ListingMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import tools.jackson.databind.json.JsonMapper

@Configuration
class ListingMQConfiguration(
    private val consumer: ListingMQConsumer,
    channel: Channel,
    jsonMapper: JsonMapper,
    publisher: Publisher,

    @param:Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @param:Value("\${koki.module.listing.mq.queue}") private val queue: String,
    @param:Value("\${koki.module.listing.mq.dlq}") private val dlq: String,
    @param:Value("\${koki.module.listing.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, jsonMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.listing.mq.dlq-cron}")
    fun processRoomDlq() {
        processDlq(queue, dlq)
    }
}
