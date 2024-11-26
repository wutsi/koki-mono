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
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import com.wutsi.koki.workflow.server.engine.command.RunActivityCommand
import com.wutsi.koki.workflow.server.service.WorkflowInstanceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkflowEngine(
    private val workflowInstanceService: WorkflowInstanceService,
    private val workflowWorker: WorkflowEngineWorker,
    private val eventPublisher: EventPublisher,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkflowEngine::class.java)
    }

    fun start(workflowInstance: WorkflowInstanceEntity): ActivityInstanceEntity? {
        LOGGER.debug(">>> ${workflowInstance.id} - Starting")

        val activityInstance = workflowWorker.start(workflowInstance)
        if (activityInstance != null) {
            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    tenantId = activityInstance.tenantId,
                )
            )
            eventPublisher.publish(
                WorkflowStartedEvent(
                    workflowInstanceId = activityInstance.workflowInstanceId,
                    tenantId = workflowInstance.tenantId,
                )
            )
        }
        return activityInstance
    }

    fun done(activityInstance: ActivityInstanceEntity, state: Map<String, Any>) {
        LOGGER.debug(">>> ${activityInstance.workflowInstanceId} > ${activityInstance.id} - Activity Done")

        val activityInstances = workflowWorker.done(activityInstance, state)
        activityInstances.forEach { instance ->
            if (instance.id == activityInstance.id) {
                if (instance.status == WorkflowStatus.DONE) {
                    eventPublisher.publish(
                        ActivityDoneEvent(
                            activityInstanceId = instance.id!!,
                            tenantId = activityInstance.tenantId,
                        )
                    )
                } else if (instance.approval == ApprovalStatus.PENDING) {
                    eventPublisher.publish(
                        ActivityDoneEvent(
                            activityInstanceId = instance.id!!,
                            tenantId = activityInstance.tenantId,
                        )
                    )
                }
            } else if (instance.status == WorkflowStatus.RUNNING) {
                eventPublisher.publish(
                    RunActivityCommand(
                        activityInstanceId = instance.id!!,
                        tenantId = activityInstance.tenantId,
                    )
                )
            }
        }
    }

    fun next(workflowInstance: WorkflowInstanceEntity): List<ActivityInstanceEntity> {
        val activityInstances = workflowWorker.next(workflowInstance)
        activityInstances.forEach { activityInstance ->
            eventPublisher.publish(
                RunActivityCommand(
                    activityInstanceId = activityInstance.id!!,
                    tenantId = activityInstance.tenantId
                )
            )
        }
        return activityInstances
    }

    fun approve(
        activityInstance: ActivityInstanceEntity,
        status: ApprovalStatus,
        approverUserId: Long,
        comment: String?,
    ): ApprovalEntity {
        LOGGER.debug(">>> ${activityInstance.workflowInstanceId} > ${activityInstance.id} - Approve status=$status")

        val approval = workflowWorker.approve(activityInstance, status, approverUserId, comment)
        if (approval.status == ApprovalStatus.APPROVED || approval.status == ApprovalStatus.REJECTED) {
            eventPublisher.publish(
                ApprovalCompletedEvent(
                    approvalId = approval.id!!,
                    activityInstanceId = activityInstance.id!!,
                    tenantId = activityInstance.tenantId,
                )
            )

            if (approval.status == ApprovalStatus.APPROVED) {
                val workflowInstance = workflowInstanceService.get(
                    activityInstance.workflowInstanceId,
                    activityInstance.tenantId
                )
                next(workflowInstance)
            }
        }

        return approval
    }

    fun done(workflowInstance: WorkflowInstanceEntity) {
        LOGGER.debug(">>> ${workflowInstance.id} - Workflow Done")

        workflowWorker.done(workflowInstance)
        eventPublisher.publish(
            WorkflowDoneEvent(
                workflowInstanceId = workflowInstance.id!!,
                tenantId = workflowInstance.tenantId,
            )
        )
    }
}
