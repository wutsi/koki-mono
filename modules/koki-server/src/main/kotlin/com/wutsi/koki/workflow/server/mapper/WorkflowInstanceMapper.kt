package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.stereotype.Service

@Service
class WorkflowInstanceMapper(
    private val activityInstanceMapper: ActivityInstanceMapper,
) {
    fun toWorkflowInstance(entity: WorkflowInstanceEntity): WorkflowInstance {
        return WorkflowInstance(
            id = entity.id ?: "",
            workflowId = entity.workflow.id ?: -1,
            approverUserId = entity.approver?.id,
            createdAt = entity.createdAt,
            startedAt = entity.startedAt,
            status = entity.status,
            dueAt = entity.dueAt,
            startAt = entity.startAt,
            state = entity.state.map { entry -> entry.name to entry.value }.toMap(),
            parameters = entity.parameters.map { entry -> entry.name to entry.value }.toMap(),
            participants = entity.participants.map { participant ->
                Participant(
                    roleId = participant.role.id ?: -1,
                    userId = participant.user.id ?: -1
                )
            },
            activityInstances = entity.activityInstances.map { activityInstance ->
                activityInstanceMapper.toActivityInstance(activityInstance)
            },
        )
    }
}
