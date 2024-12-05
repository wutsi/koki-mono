package com.wutsi.koki.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.event.server.rabbitmq.RabbitMQConsumer
import com.wutsi.koki.event.server.rabbitmq.RabbitMQEventPublisher
import com.wutsi.koki.event.server.service.EventPublisher
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.util.Timer
import java.util.TimerTask

@Configuration
@ConditionalOnProperty(
    value = ["koki.event-publisher.type"],
    havingValue = "rabbitmq",
)
class RabbitMQWorkflowConfiguration(
    private val channel: Channel,
    private val objectMapper: ObjectMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher,

    @Value("\${koki.event-publisher.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.workflow-engine.rabbitmq.queue}") private val queue: String,
    @Value("\${koki.workflow-engine.rabbitmq.dlq}") private val dlq: String,
    @Value("\${koki.workflow-engine.rabbitmq.consumer-delay-seconds}") private val consumerDelay: Int,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQWorkflowConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        // DLQ
        LOGGER.info("Setup DLQ: $dlq")
        channel.queueDeclare(
            dlq,
            true, // durable
            false, // exclusive
            false, // autoDelete
            mapOf(),
        )

        // Queue
        LOGGER.info("Setup queue: $queue")
        channel.queueDeclare(
            queue,
            true, // durable
            false, // exclusive
            false, // autoDelete
            mapOf(
                "x-dead-letter-exchange" to "",
                "x-dead-letter-routing-key" to dlq,
            ),
        )

        // Link queue with exchange
        LOGGER.info("Linking: $exchangeName -> $queue")
        channel.queueBind(
            queue,
            exchangeName,
            "",
        )

        // Consumer
        setupConsumer()
    }

    @Scheduled(cron = "\${koki.workflow-engine.rabbitmq.dlq-cron}")
    fun processDLQ() {
        if (eventPublisher is RabbitMQEventPublisher) {
            eventPublisher.processDlq(
                queue = queue,
                dlq = dlq,
            )
        }
    }

    private fun setupConsumer() {
        /*
        Wait before registering the consumer, so that the server is completely UP.
        With Spring, this caused by bug because the consumer was registered during the startup,
        then it dispatched the event using EventListener, but the event was lost because all the
        event listener was not yet setup by spring!
         */
        LOGGER.info("Will setup queue consumer in $consumerDelay seconds(s)")
        val task = object : TimerTask() {
            override fun run() {
                LOGGER.info("Registering queue consumer")
                channel.basicConsume(
                    queue,
                    false, // auto-ack
                    RabbitMQConsumer(
                        objectMapper = objectMapper,
                        applicationEventPublisher = applicationEventPublisher,
                        channel = channel,
                    ),
                )
            }
        }
        Timer(queue, false).schedule(task, 1000L * consumerDelay)
    }
}
