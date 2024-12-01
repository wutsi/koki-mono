package com.wutsi.koki.workflow.server.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceMapper(
    private val activityInstanceMapper: ActivityInstanceMapper,
    private val objectMapper: ObjectMapper,
) {
    fun toWorkflowInstance(entity: WorkflowInstanceEntity): WorkflowInstance {
        return WorkflowInstance(
            id = entity.id!!,
            workflowId = entity.workflowId,
            approverUserId = entity.approverId,
            title = entity.title ?: "",
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            status = entity.status,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            doneAt = entity.doneAt,
            state = entity.stateAsMap(objectMapper),
            parameters = entity.parametersAsMap(objectMapper),
            participants = entity.participants.map { participant ->
                Participant(
                    roleId = participant.roleId,
                    userId = participant.userId
                )
            },
            activityInstances = entity.activityInstances.map { activityInstance ->
                activityInstanceMapper.toActivityInstanceSummary(activityInstance)
            },
            createdById = entity.createdById,
        )
    }

    fun toWorkflowInstanceSummary(entity: WorkflowInstanceEntity): WorkflowInstanceSummary {
        return WorkflowInstanceSummary(
            id = entity.id ?: "",
            workflowId = entity.workflowId,
            approverUserId = entity.approverId,
            title = entity.title ?: "",
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            status = entity.status,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            createdById = entity.createdById,
            doneAt = entity.doneAt,
        )
    }
}
