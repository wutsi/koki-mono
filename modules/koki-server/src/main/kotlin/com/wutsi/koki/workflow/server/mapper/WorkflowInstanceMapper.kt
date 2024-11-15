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
    @Suppress("UNCHECKED_CAST")
    fun toWorkflowInstance(entity: WorkflowInstanceEntity): WorkflowInstance {
        return WorkflowInstance(
            id = entity.id!!,
            workflowId = entity.workflowId,
            approverUserId = entity.approverId,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            status = entity.status,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            state = entity.state?.let { state ->
                objectMapper.readValue(state, Map::class.java) as Map<String, Any>
            } ?: emptyMap(),
            parameters = entity.parameters?.let { parameters ->
                objectMapper.readValue(parameters, Map::class.java) as Map<String, String>
            } ?: emptyMap(),
            participants = entity.participants.map { participant ->
                Participant(
                    roleId = participant.roleId,
                    userId = participant.userId
                )
            },
            activityInstances = entity.activityInstances.map { activityInstance ->
                activityInstanceMapper.toActivityInstanceSummary(activityInstance)
            },
        )
    }

    fun toWorkflowInstanceSummary(entity: WorkflowInstanceEntity): WorkflowInstanceSummary {
        return WorkflowInstanceSummary(
            id = entity.id ?: "",
            workflowId = entity.workflowId,
            approverUserId = entity.approverId,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            status = entity.status,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
        )
    }
}
