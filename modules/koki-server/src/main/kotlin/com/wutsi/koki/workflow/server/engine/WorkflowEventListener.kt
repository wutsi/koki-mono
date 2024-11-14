package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.event.FormUpdatedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class WorkflowEventListener {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEventListener::class.java)
    }

    @EventListener
    fun onFormSubmitted(event: FormSubmittedEvent) {
        LOGGER.info(">>> received event: $event")
    }

    @EventListener
    fun onFormUpdated(event: FormUpdatedEvent) {
        LOGGER.info(">>> received event: $event")
    }
}
