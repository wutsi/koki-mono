package com.wutsi.koki.tracking.server.config

import com.rabbitmq.client.Channel
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tracking.server.service.TrackingConsumer
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import tools.jackson.databind.json.JsonMapper

@Configuration
class TrackingMQConfiguration(
    private val consumer: TrackingConsumer,
    channel: Channel,
    jsonMapper: JsonMapper,
    publisher: Publisher,

    @Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.module.tracking.mq.queue}") private val queue: String,
    @Value("\${koki.module.tracking.mq.dlq}") private val dlq: String,
    @Value("\${koki.module.tracking.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, jsonMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.tracking.mq.dlq-cron}")
    fun processNotificationDlq() {
        processDlq(queue, dlq)
    }
}
