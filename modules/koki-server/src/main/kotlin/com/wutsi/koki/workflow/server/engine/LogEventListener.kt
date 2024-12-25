package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.event.ActivityDoneEvent
import com.wutsi.koki.workflow.dto.event.ActivityStartedEvent
import com.wutsi.koki.workflow.dto.event.ApprovalDoneEvent
import com.wutsi.koki.workflow.dto.event.ApprovalStartedEvent
import com.wutsi.koki.workflow.dto.event.WorkflowDoneEvent
import com.wutsi.koki.workflow.dto.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.server.service.LogService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class LogEventListener(
    private val logService: LogService,
    private val logger: KVLogger,
) : RabbitMQHandler {
    override fun handle(event: Any): Boolean {
        if (event is WorkflowStartedEvent) {
            onWorkflowStarted(event)
        } else if (event is WorkflowDoneEvent) {
            onWorkflowDone(event)
        } else if (event is ApprovalStartedEvent) {
            onApprovalStarted(event)
        } else if (event is ApprovalDoneEvent) {
            onApprovalDone(event)
        } else if (event is ActivityDoneEvent) {
            onActivityDone(event)
        } else if (event is ActivityStartedEvent) {
            onActivityStartedEvent(event)
        } else {
            return false
        }

        logger.add("event_classname", event::class.java.simpleName)
        logger.add("listener", "LogEventListener")
        return true
    }

    @EventListener
    fun onWorkflowStarted(event: WorkflowStartedEvent) {
        logService.info(
            message = "Workflow started",
            workflowInstanceId = event.workflowInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onWorkflowDone(event: WorkflowDoneEvent) {
        logService.info(
            message = "Workflow done",
            workflowInstanceId = event.workflowInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onActivityDone(event: ActivityDoneEvent) {
        logService.info(
            message = "Activity done",
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onApprovalStarted(event: ApprovalStartedEvent) {
        logService.info(
            message = "Activity approval started",
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onApprovalDone(event: ApprovalDoneEvent) {
        val message = if (event.status == ApprovalStatus.APPROVED) {
            "Activity approved"
        } else {
            "Activity rejected"
        }
        logService.info(
            message = message,
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onActivityStartedEvent(command: ActivityStartedEvent) {
        logService.info(
            message = "Activity started",
            workflowInstanceId = command.workflowInstanceId,
            activityInstanceId = command.activityInstanceId,
            tenantId = command.tenantId,
            timestamp = command.timestamp,
        )
    }
}
