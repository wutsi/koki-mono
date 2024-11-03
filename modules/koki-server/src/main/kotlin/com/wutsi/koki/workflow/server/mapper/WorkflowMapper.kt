package com.wutsi.koki.workflow.server.mapper

import com.wutsi.koki.tenant.server.domain.RoleEntity
import com.wutsi.koki.tenant.server.mapper.RoleMapper
import com.wutsi.koki.tenant.server.service.RoleService
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val activityMapper: ActivityMapper,
    private val roleService: RoleService,
    private val roleMapper: RoleMapper,
) {
    fun toWorkflow(entity: WorkflowEntity): Workflow {
        val roles = getRoles(entity).map { role -> roleMapper.toRole(role) }
        val activities = entity.activities.map { activity -> activityMapper.toActivity(activity) }
        return Workflow(
            id = entity.id ?: -1,
            name = entity.name,
            description = entity.description,
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            activities = activities,
            roles = roles,
            requiresApprover = activities.find { activity -> activity.requiresApproval } != null,
            parameters = entity?.parameters?.split(",")?.map { param -> param.trim() } ?: emptyList()
        )
    }

    private fun getRoles(entity: WorkflowEntity): List<RoleEntity> {
        val roleIds = entity.activities.mapNotNull { activity -> activity.role?.id }.toSet()
        return if (roleIds.isEmpty()) {
            emptyList()
        } else {
            roleService.getAll(roleIds.toList(), entity.tenant.id ?: -1).sortedBy { it.id }
        }
    }
}
