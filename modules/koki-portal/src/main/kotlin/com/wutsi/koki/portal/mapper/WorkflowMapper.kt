package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class WorkflowMapper(
    private val userMapper: UserMapper
) {
    fun toWorkflowModel(entity: WorkflowSummary): WorkflowModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return WorkflowModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            requiresApprover = entity.requiresApprover,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toWorkflowModel(
        entity: Workflow,
        approverRole: Role?,
        roles: List<Role>,
        imageUrl: String
    ): WorkflowModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")
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
            },
            roles = roles.map { role -> userMapper.toRoleModel(role) },
            parameters = entity.parameters,
            approverRole = approverRole?.let { role -> userMapper.toRoleModel(role) },
            workflowInstanceCount = entity.workflowInstanceCount,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAtText = fmt.format(entity.modifiedAt),
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
