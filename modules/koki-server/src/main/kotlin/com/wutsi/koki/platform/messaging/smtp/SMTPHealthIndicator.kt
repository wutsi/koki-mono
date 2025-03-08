package com.wutsi.koki.platform.messaging.smtp

import jakarta.mail.Session
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class SMTPHealthIndicator(
    private val session: Session
) : HealthIndicator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMTPHealthIndicator::class.java)
    }

    override fun health(): Health {
        try {
            val now = System.currentTimeMillis()
            val transport = session.getTransport("smtp")
            try {
                transport.connect()
                return Health.up()
                    .withDetail("durationMillis", System.currentTimeMillis() - now)
                    .build()
            } finally {
                transport.close()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Healthcheck error", ex)
            return Health.down()
                .withException(ex)
                .build()
        }
    }
}
