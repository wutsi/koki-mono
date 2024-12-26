package com.wutsi.koki.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.wutsi.koki.event.server.rabbitmq.RabbitMQConsumer
import com.wutsi.koki.event.server.rabbitmq.RabbitMQEventPublisher
import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.workflow.server.engine.LogEventListener
import com.wutsi.koki.workflow.server.engine.NotificationEventListener
import com.wutsi.koki.workflow.server.engine.WorkflowEventListener
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
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
    private val workflowEngineListener: WorkflowEventListener,
    private val logEventListener: LogEventListener,
    private val notificationEventListener: NotificationEventListener,
    private val eventPublisher: EventPublisher,

    @Value("\${koki.event-publisher.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.workflow-engine.rabbitmq.workflow-queue}") private val workflowQueue: String,
    @Value("\${koki.workflow-engine.rabbitmq.workflow-dlq}") private val workflowDlq: String,
    @Value("\${koki.workflow-engine.rabbitmq.log-queue}") private val logQueue: String,
    @Value("\${koki.workflow-engine.rabbitmq.log-dlq}") private val logDlq: String,
    @Value("\${koki.workflow-engine.rabbitmq.notification-queue}") private val notificationQueue: String,
    @Value("\${koki.workflow-engine.rabbitmq.notification-dlq}") private val notificationDlq: String,
    @Value("\${koki.workflow-engine.rabbitmq.consumer-delay-seconds}") private val consumerDelay: Int,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQWorkflowConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        setupQueues(workflowQueue, workflowDlq)
        setupConsumer(workflowQueue, workflowEngineListener)

        setupQueues(logQueue, logDlq)
        setupConsumer(logQueue, logEventListener)

        setupQueues(notificationQueue, notificationDlq)
        setupConsumer(notificationQueue, notificationEventListener)
    }

    @Scheduled(cron = "\${koki.workflow-engine.rabbitmq.dlq-cron}")
    fun processWorkflowDLQ() {
        processDlq(workflowQueue, workflowDlq)
    }

    @Scheduled(cron = "\${koki.workflow-engine.rabbitmq.dlq-cron}")
    fun processLogDLQ() {
        processDlq(logQueue, logDlq)
    }

    @Scheduled(cron = "\${koki.workflow-engine.rabbitmq.dlq-cron}")
    fun processNotificationDLQ() {
        processDlq(notificationQueue, notificationDlq)
    }

    private fun processDlq(queue: String, dlq: String) {
        LOGGER.info("Processing DLQ: $dlq -> $queue")
        if (eventPublisher is RabbitMQEventPublisher) {
            eventPublisher.processDlq(queue = queue, dlq = dlq)
        }
    }

    private fun setupConsumer(queue: String, handler: RabbitMQHandler) {
        /*
        Wait before registering the consumer, so that the server is completely UP.
        With Spring, this caused by bug because the consumer was registered during the startup,
        then it dispatched the event using EventListener, but the event was lost because all the
        event listener was not yet setup by spring!
         */
        LOGGER.info("Will setup $queue consumer in $consumerDelay seconds(s)")
        val task = object : TimerTask() {
            override fun run() {
                LOGGER.info("Registering queue consumer")
                channel.basicConsume(
                    queue,
                    false, // auto-ack
                    RabbitMQConsumer(
                        objectMapper = objectMapper,
                        handler = handler,
                        channel = channel,
                    ),
                )
            }
        }
        Timer(queue, false).schedule(task, 1000L * consumerDelay)
    }

    private fun setupQueues(queue: String, dlq: String) {
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
    }
}
