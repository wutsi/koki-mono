package com.wutsi.koki.platform.mq.rabbitmq

import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator

class RabbitMQHealthIndicator(
    private val channel: Channel,
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQHealthIndicator::class.java)
    }

    override fun health(): Health {
        try {
            val now = System.currentTimeMillis()
            val version = channel.connection.serverProperties.get("version").toString()
            return Health.up()
                .withDetail("version", version)
                .withDetail("durationMillis", System.currentTimeMillis() - now)
                .build()
        } catch (ex: Exception) {
            LOGGER.warn("Healthcheck error", ex)
            return Health.down()
                .withException(ex)
                .build()
        }
    }
}
