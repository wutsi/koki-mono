package com.wutsi.koki.file.server.config

import com.rabbitmq.client.Channel
import com.wutsi.koki.config.AbstractRabbitMQConsumerConfiguration
import com.wutsi.koki.file.server.service.mq.FileMQConsumer
import com.wutsi.koki.platform.mq.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import tools.jackson.databind.json.JsonMapper

@Configuration
class FileMQConfiguration(
    private val invoiceConsumer: FileMQConsumer,
    channel: Channel,
    jsonMapper: JsonMapper,
    publisher: Publisher,

    @param:Value("\${wutsi.platform.mq.rabbitmq.exchange-name}") private val exchangeName: String,
    @param:Value("\${koki.module.file.mq.queue}") private val queue: String,
    @param:Value("\${koki.module.file.mq.dlq}") private val dlq: String,
    @param:Value("\${koki.module.file.mq.consumer-delay-seconds}") private val consumerDelay: Int,
) : AbstractRabbitMQConsumerConfiguration(channel, jsonMapper, publisher) {
    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, invoiceConsumer, consumerDelay)
    }

    @Scheduled(cron = "\${koki.module.file.mq.dlq-cron}")
    fun processNotificationDlq() {
        processDlq(queue, dlq)
    }
}
