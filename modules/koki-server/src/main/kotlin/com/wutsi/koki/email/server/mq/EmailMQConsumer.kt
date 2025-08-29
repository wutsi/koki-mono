package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.mq.Consumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmailMQConsumer : Consumer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmailMQConsumer::class.java)
    }

    private val workers: MutableList<EmailWorker> = mutableListOf()

    fun register(worker: EmailWorker) {
        LOGGER.info("Registering worker: ${worker::class.java.name}")
        workers.add(worker)
    }

    fun unregister(worker: EmailWorker) {
        LOGGER.info("Unregistering worker: ${worker::class.java.name}")
        workers.remove(worker)
    }

    override fun consume(event: Any): Boolean {
        workers.forEach { worker ->
            if (worker.notify(event)) {
                return true
            }
        }
        return false
    }
}
