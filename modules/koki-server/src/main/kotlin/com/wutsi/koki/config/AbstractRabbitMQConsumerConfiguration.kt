package com.wutsi.koki.config

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.mq.rabbitmq.RabbitMQConsumer
import com.wutsi.koki.platform.mq.rabbitmq.RabbitMQPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tools.jackson.databind.json.JsonMapper
import java.util.Timer
import java.util.TimerTask

abstract class AbstractRabbitMQConsumerConfiguration(
    private val channel: Channel,
    private val jsonMapper: JsonMapper,
    private val publisher: Publisher,
) {
    protected fun processDlq(queue: String, dlq: String) {
        getLogger().info("Processing DLQ: $dlq -> $queue")
        if (publisher is RabbitMQPublisher) {
            publisher.processDlq(queue = queue, dlq = dlq)
        }
    }

    protected fun setupConsumer(queue: String, consumer: Consumer, consumerDelay: Int) {
        /*
        Wait before registering the consumer, so that the server is completely UP.
        With Spring, this caused by bug because the consumer was registered during the startup,
        then it dispatched the event using EventListener, but the event was lost because all the
        event listener was not yet setup by spring!
         */
        val logger = getLogger()
        logger.info("Will setup $queue consumer in $consumerDelay seconds(s)")
        val task = object : TimerTask() {
            override fun run() {
                logger.info("Registering queue consumer")
                channel.basicConsume(
                    queue,
                    false, // auto-ack
                    RabbitMQConsumer(
                        jsonMapper = jsonMapper,
                        delegate = consumer,
                        channel = channel,
                    ),
                )
            }
        }
        Timer(queue, false).schedule(task, 1000L * consumerDelay)
    }

    fun setupExchange(exchangeName: String) {
        val logger = getLogger()
        logger.info("setup exchange $exchangeName")

        channel.exchangeDeclare(
            exchangeName,
            BuiltinExchangeType.FANOUT,
            true, // durable
        )
    }

    protected fun setupQueue(queue: String, dlq: String, exchangeName: String) {
        val logger = getLogger()

        // DLQ
        logger.info("Setup DLQ: $dlq")
        channel.queueDeclare(
            dlq,
            true, // durable
            false, // exclusive
            false, // autoDelete
            mapOf(),
        )

        // Queue
        logger.info("Setup queue: $queue")
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
        logger.info("Linking: $exchangeName -> $queue")
        channel.queueBind(
            queue,
            exchangeName,
            "",
        )
    }

    private fun getLogger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}
