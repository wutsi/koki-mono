package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.event.server.rabbitmq.RabbitMQHandler
import com.wutsi.koki.form.event.ActivityDoneEvent
import com.wutsi.koki.form.event.ApprovalCompletedEvent
import com.wutsi.koki.form.event.ApprovalStartedEvent
import com.wutsi.koki.form.event.WorkflowDoneEvent
import com.wutsi.koki.form.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.LogEntryType
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ApprovalService
import com.wutsi.koki.workflow.server.service.LogService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class LogListener(private val logService: LogService) : RabbitMQHandler {
    override fun handle(event: Any) {
        if (event is WorkflowStartedEvent) {
            onWorkflowStarted(event)
        } else if (event is WorkflowDoneEvent) {
            onWorkflowDone(event)
        } else if (event is ApprovalStartedEvent) {
            onApprovalStarted(event)
        } else if (event is ApprovalCompletedEvent) {
            onApprovalCompleted(event)
        } else if (event is ActivityDoneEvent) {
            onActivityDone(event)
        } else if (event is RunActivityCommand) {
            onRunActivityCommand(event)
        }
    }

    @EventListener
    fun onWorkflowStarted(event: WorkflowStartedEvent) {
        info(
            message = "Workflow starting",
            workflowInstanceId = event.workflowInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onWorkflowDone(event: WorkflowDoneEvent) {
        info(
            message = "Workflow done",
            workflowInstanceId = event.workflowInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onActivityDone(event: ActivityDoneEvent) {
        info(
            message = "Activity done",
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onApprovalStarted(event: ApprovalStartedEvent) {
        info(
            message = "Activity approval starting",
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onApprovalCompleted(event: ApprovalCompletedEvent) {
        val message = if (event.status == ApprovalStatus.APPROVED) {
            "Activity approved"
        } else {
            "Activity rejected"
        }
        info(
            message = message,
            workflowInstanceId = event.workflowInstanceId,
            activityInstanceId = event.activityInstanceId,
            tenantId = event.tenantId,
            timestamp = event.timestamp,
        )
    }

    @EventListener
    fun onRunActivityCommand(command: RunActivityCommand) {
        info(
            message = "Activity starting",
            workflowInstanceId = command.workflowInstanceId,
            activityInstanceId = command.activityInstanceId,
            tenantId = command.tenantId,
            timestamp = command.timestamp,
        )
    }

    private fun info(
        message: String,
        workflowInstanceId: String,
        tenantId: Long,
        timestamp: Long,
        activityInstanceId: String? = null,
    ) {
        logService.create(
            tenantId = tenantId,
            type = LogEntryType.INFO,
            message = message,
            workflowInstanceId = workflowInstanceId,
            activityInstanceId = activityInstanceId,
            timestamp = timestamp,
        )
    }
}
