package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val activityMapper: ActivityMapper,
    private val flowMapper: FlowMapper
) {
    fun toWorkflow(entity: WorkflowEntity): Workflow {
        val roles = getRoles(entity)
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
            roleIds = roles.map { role -> role.id ?: -1 },
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

    private fun getRoles(entity: WorkflowEntity): List<RoleEntity> {
        return entity.activities
            .mapNotNull { activity -> activity.role }
            .distinctBy { role -> role.id }
            .sortedBy { role -> role.id }
    }
}
