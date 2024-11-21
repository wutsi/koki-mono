package com.wutsi.koki.portal.mapper

import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.RoleModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class WorkflowMapper {
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
        approverRole: RoleModel?,
        roles: List<RoleModel>,
        forms: List<FormModel>,
        imageUrl: String
    ): WorkflowModel {
        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val roleMap = roles.associateBy { role -> role.id }
        val formMap = forms.associateBy { form -> form.id }
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
                val form = activity.formId?.let { id -> formMap[id] }
                toActivityModel(activity, role, form)
            },
            roles = roles,
            parameters = entity.parameters,
            approverRole = approverRole,
            workflowInstanceCount = entity.workflowInstanceCount,
            createdAtText = fmt.format(entity.createdAt),
            modifiedAtText = fmt.format(entity.modifiedAt),
        )
    }

    fun toActivityModel(
        entity: Activity,
        role: RoleModel?,
        form: FormModel?,
    ): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
            description = entity.description ?: "",
            requiresApproval = entity.requiresApproval,
            role = role,
            form = form,
        )
    }

    fun toActivityModel(entity: ActivitySummary): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
            form = entity.formId?.let { id -> FormModel(id = id) },
            role = entity.roleId?.let { id -> RoleModel(id = id) },
        )
    }
}
