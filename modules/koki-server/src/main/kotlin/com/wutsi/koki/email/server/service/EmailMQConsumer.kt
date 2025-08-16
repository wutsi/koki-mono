package com.wutsi.koki.notification.server.service

import com.wutsi.koki.platform.mq.Consumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationMQConsumer : Consumer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(NotificationMQConsumer::class.java)
    }

    private val workers: MutableList<NotificationWorker> = mutableListOf()

    fun register(worker: NotificationWorker) {
        LOGGER.info("Registering worker: ${worker::class.java.name}")
        workers.add(worker)
    }

    fun unregister(worker: NotificationWorker) {
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
