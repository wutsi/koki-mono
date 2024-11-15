package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val activityMapper: ActivityMapper,
    private val flowMapper: FlowMapper,
) {
    fun toWorkflow(entity: WorkflowEntity): Workflow {
        val activities = entity.activities.map { activity -> activityMapper.toActivity(activity) }
        return Workflow(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            activities = activities,
            roleIds = entity.activities
                .mapNotNull { activity -> activity.roleId }
                .distinctBy { roleId -> roleId }
                .sorted(),
            requiresApprover = activities.find { activity -> activity.requiresApproval } != null,
            parameters = entity.parameterAsList(),
            flows = entity.flows.map { flow -> flowMapper.toFlow(flow) }
        )
    }

    fun toWorkflowSummary(entity: WorkflowEntity): WorkflowSummary {
        return WorkflowSummary(
            id = entity.id!!,
            name = entity.name,
            title = entity.title,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
