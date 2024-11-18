package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(
    private val userMapper: UserMapper
) {
    fun toWorkflowModel(entity: Workflow, roles: List<Role>, imageUrl: String): WorkflowModel {
        val roleMap = roles.associateBy { role -> role.id }
        return WorkflowModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            description = entity.description ?: "",
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            requiresApprover = entity.requiresApprover,
            imageUrl = imageUrl,
            activities = entity.activities.map { activity ->
                val role = activity.roleId?.let { id -> roleMap[id] }
                toActivityModel(activity, role)
            }
        )
    }

    fun toWorkflowModel(entity: WorkflowSummary): WorkflowModel {
        return WorkflowModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            requiresApprover = entity.requiresApprover,
        )
    }

    fun toActivityModel(entity: Activity, role: Role?): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
            description = entity.description ?: "",
            requiresApproval = entity.requiresApproval,
            role = role?.let { userMapper.toRoleModel(role) }
        )
    }

    fun toActivityModel(entity: ActivitySummary): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
        )
    }
}
