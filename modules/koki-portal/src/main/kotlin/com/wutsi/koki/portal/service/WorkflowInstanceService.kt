package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.WorkflowInstanceMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.ActivityInstanceModel
import com.wutsi.koki.portal.page.workflow.StartWorkflowForm
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.workflow.dto.CreateWorkflowInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceService(
    private val kokiWorkflow: KokiWorkflow,
    private val kokiWorkflowInstance: KokiWorkflowInstance,
    private val currentUserHolder: CurrentUserHolder,
    private val workflowMapper: WorkflowMapper,
    private val workflowInstanceMapper: WorkflowInstanceMapper,
) {
    fun create(form: StartWorkflowForm): String {
        return kokiWorkflowInstance.create(
            CreateWorkflowInstanceRequest(
                workflowId = form.workflowId,
                participants = form.participants,
                approverUserId = form.approverUserId,
                startAt = form.startAt,
                dueAt = form.dueAt,
                parameters = form.parameters,
            )
        ).workflowInstanceId
    }

    fun myActivities(): List<ActivityInstanceModel> {
        val id = currentUserHolder.id() ?: return emptyList()

        // Activity Instances
        val activityInstances = kokiWorkflowInstance.activities(
            assigneeIds = listOf(id),
            status = WorkflowStatus.RUNNING
        ).activityInstances
        if (activityInstances.isEmpty()) {
            return emptyList()
        }

        // Activities
        val activityIds = activityInstances.map { activityInstance -> activityInstance.activityId }
            .toSet()
            .toList()
        val activityMap = kokiWorkflow.activities(
            ids = activityIds,
            limit = activityIds.size
        )
            .activities
            .map { activity -> workflowMapper.toActivityModel(activity) }
            .associateBy { activity -> activity.id }

        // User
        val me = currentUserHolder.get()
        return activityInstances.map { activityInstance ->
            workflowInstanceMapper.toActivityInstanceModel(
                entity = activityInstance,
                activity = activityMap[activityInstance.activityId]!!,
                assignee = me
            )
        }
    }
}
