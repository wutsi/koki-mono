package com.wutsi.koki.workflow.server.engine

import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.form.event.ActivityDoneEvent
import com.wutsi.koki.form.event.ApprovalCompletedEvent
import com.wutsi.koki.form.event.WorkflowDoneEvent
import com.wutsi.koki.form.event.WorkflowStartedEvent
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.domain.ApprovalEntity
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.ActivityInstanceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowEngine(
    private val activityInstanceService: ActivityInstanceService,
    private val workflowWorker: WorkflowEngineWorker,
    private val eventPublisher: EventPublisher,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEngine::class.java)
    }

    fun start(workflowInstanceId: String, tenantId: Long): ActivityInstanceEntity? {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("start (workflowInstanceId=$workflowInstanceId, tenantId=$tenantId)")
        }

        val activityInstance = workflowWorker.start(workflowInstanceId, tenantId)
        if (activityInstance != null) {
            eventPublisher.publish(
                WorkflowStartedEvent(
                    workflowInstanceId = workflowInstanceId,
                    tenantId = tenantId,
                )
            )

            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    workflowInstanceId = activityInstance.workflowInstanceId,
                    tenantId = activityInstance.tenantId,
                )
            )
        }
        return activityInstance
    }

    fun done(activityInstanceId: String, state: Map<String, Any>, tenantId: Long) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("done (activityInstanceId=$activityInstanceId, state=$state, tenantId=$tenantId)")
        }

        val activityInstances = workflowWorker.done(activityInstanceId, state, tenantId)
        activityInstances.forEach { instance ->
            if (instance.id == activityInstanceId) {
                if (instance.status == WorkflowStatus.DONE) {
                    eventPublisher.publish(
                        ActivityDoneEvent(
                            activityInstanceId = activityInstanceId,
                            workflowInstanceId = instance.workflowInstanceId,
                            tenantId = tenantId,
                        )
                    )
                } else if (instance.approval == ApprovalStatus.PENDING) {
                    eventPublisher.publish(
                        ActivityDoneEvent(
                            activityInstanceId = activityInstanceId,
                            workflowInstanceId = instance.workflowInstanceId,
                            tenantId = tenantId,
                        )
                    )
                }
            } else if (instance.status == WorkflowStatus.RUNNING) {
                eventPublisher.publish(
                    RunActivityCommand(
                        activityInstanceId = instance.id!!,
                        workflowInstanceId = instance.workflowInstanceId,
                        tenantId = tenantId,
                    )
                )
            }
        }
    }

    fun next(workflowInstanceId: String, tenantId: Long): List<ActivityInstanceEntity> {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("next (workflowInstanceId=$workflowInstanceId, tenantId=$tenantId)")
        }

        val activityInstances = workflowWorker.next(workflowInstanceId, tenantId)
        activityInstances.forEach { activityInstance ->
            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    workflowInstanceId = activityInstance.workflowInstanceId,
                    tenantId = activityInstance.tenantId
                )
            )
        }
        return activityInstances
    }

    fun approve(
        activityInstanceId: String,
        status: ApprovalStatus,
        approverUserId: Long,
        comment: String?,
        tenantId: Long,
    ): ApprovalEntity {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("approve (activityInstanceId=$activityInstanceId, status=$status, approverUserId=$approverUserId, tenantId=$tenantId)")
        }

        val activityInstance = activityInstanceService.get(activityInstanceId, tenantId)
        val approval = workflowWorker.approve(activityInstanceId, status, approverUserId, comment, tenantId)
        if (approval.status == ApprovalStatus.APPROVED || approval.status == ApprovalStatus.REJECTED) {
            eventPublisher.publish(
                ApprovalCompletedEvent(
                    approvalId = approval.id!!,
                    activityInstanceId = activityInstanceId,
                    workflowInstanceId = activityInstance.workflowInstanceId,
                    status = approval.status,
                    tenantId = tenantId,
                )
            )

            if (approval.status == ApprovalStatus.APPROVED) {
                next(activityInstance.workflowInstanceId, tenantId)
            }
        }

        return approval
    }

    fun done(workflowInstanceId: String, tenantId: Long) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("done (workflowInstanceId=$workflowInstanceId, tenantId=$tenantId)")
        }

        workflowWorker.done(workflowInstanceId, tenantId)
        eventPublisher.publish(
            WorkflowDoneEvent(
                workflowInstanceId = workflowInstanceId,
                tenantId = tenantId,
            )
        )
    }
}
