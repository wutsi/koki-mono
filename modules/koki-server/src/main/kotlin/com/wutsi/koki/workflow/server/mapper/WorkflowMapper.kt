package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.mapper.RoleMapper
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val activityMapper: ActivityMapper,
    private val roleMapper: RoleMapper,
) {
    fun toWorkflow(entity: WorkflowEntity): Workflow {
        val roles = getRoles(entity).map { role -> roleMapper.toRole(role) }
        val activities = entity.activities.map { activity -> activityMapper.toActivity(activity) }
        return Workflow(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            activities = activities,
            roles = roles,
            requiresApprover = activities.find { activity -> activity.requiresApproval } != null,
            parameters = entity.parameterAsList()
        )
    }

    fun toWorkflowSummary(entity: WorkflowEntity): WorkflowSummary {
        return WorkflowSummary(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
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
