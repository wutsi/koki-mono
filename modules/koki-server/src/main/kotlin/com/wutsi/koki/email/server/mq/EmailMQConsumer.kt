package com.wutsi.koki.email.server.mq

import com.wutsi.koki.platform.mq.Consumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmailMQConsumer : Consumer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmailMQConsumer::class.java)
    }

    private val mailets: MutableList<Mailet> = mutableListOf()

    fun register(mailet: Mailet) {
        LOGGER.info("Registering mailet: ${mailet::class.java.name}")
        mailets.add(mailet)
    }

    fun unregister(mailet: Mailet) {
        LOGGER.info("Unregistering mailer: ${mailet::class.java.name}")
        mailets.remove(mailet)
    }

    override fun consume(event: Any): Boolean {
        mailets.forEach { worker ->
            if (worker.service(event)) {
                return true
            }
        }
        return false
    }
}
