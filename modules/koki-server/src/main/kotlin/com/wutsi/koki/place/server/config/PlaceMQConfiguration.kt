package com.wutsi.koki.place.server.config

import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.place.server.service.mq.PlaceMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import tools.jackson.databind.json.JsonMapper

@Configuration
class PlaceMQConfiguration(
    private val consumer: PlaceMQConsumer,
    channel: Channel,
    jsonMapper: JsonMapper,
    publisher: Publisher,

    @param:Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @param:Value("\${koki.module.place.mq.queue}") private val queue: String,
    @param:Value("\${koki.module.place.mq.dlq}") private val dlq: String,
    @param:Value("\${koki.module.place.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, jsonMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.place.mq.dlq-cron}")
    fun processRoomDlq() {
        processDlq(queue, dlq)
    }
}
