package com.wutsi.koki.ai.server.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AIMQConsumer(
    private val logger: KVLogger
) : Consumer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AIMQConsumer::class.java)
    }

    private val agents: MutableList<AIAgent> = mutableListOf()

    fun register(agent: AIAgent) {
        LOGGER.info("Registering agent: ${agent::class.java.name}")
        agents.add(agent)
    }

    fun unregister(agent: AIAgent) {
        LOGGER.info("Unregistering agent: ${agent::class.java.name}")
        agents.remove(agent)
    }

    override fun consume(event: Any): Boolean {
        agents.forEach { worker ->
            val duration = System.currentTimeMillis()
            try {
                if (worker.notify(event)) {
                    return true
                }
            } finally {
                logger.add("duration", System.currentTimeMillis() - duration)
            }
        }
        return false
    }
}
