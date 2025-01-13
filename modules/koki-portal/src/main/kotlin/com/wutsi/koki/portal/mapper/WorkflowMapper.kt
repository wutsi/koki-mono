package com.wutsi.koki.portal.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.FormModel
import com.wutsi.koki.portal.model.MessageModel
import com.wutsi.koki.portal.model.RecipientModel
import com.wutsi.koki.portal.model.ScriptModel
import com.wutsi.koki.portal.model.ServiceModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.user.model.RoleModel
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowSummary
import org.springframework.stereotype.Service

@Service
class WorkflowMapper(private val objectMapper: ObjectMapper) : TenantAwareMapper() {
    fun toWorkflowModel(entity: WorkflowSummary): WorkflowModel {
        val fmt = createDateFormat()
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
        messages: List<MessageModel>,
        scripts: List<ScriptModel>,
        services: List<ServiceModel>,
        imageUrl: String
    ): WorkflowModel {
        val fmt = createDateFormat()
        val roleMap = roles.associateBy { role -> role.id }
        val formMap = forms.associateBy { form -> form.id }
        val messageMap = messages.associateBy { message -> message.id }
        val scriptMap = scripts.associateBy { script -> script.id }
        val serviceMap = services.associateBy { service -> service.id }
        return WorkflowModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: "",
            description = entity.description?.trim()?.ifEmpty { null },
            active = entity.active,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            requiresApprover = entity.requiresApprover,
            imageUrl = imageUrl,
            activities = entity.activities.map { activity ->
                val role = activity.roleId?.let { id -> roleMap[id] }
                val form = activity.formId?.let { id -> formMap[id] }
                val message = activity.messageId?.let { id -> messageMap[id] }
                val script = activity.scriptId?.let { id -> scriptMap[id] }
                val service = activity.serviceId?.let { id -> serviceMap[id] }
                toActivityModel(activity, role, form, message, script, service)
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
        message: MessageModel?,
        script: ScriptModel?,
        service: ServiceModel?,
    ): ActivityModel {
        return ActivityModel(
            id = entity.id,
            workflowId = entity.workflowId,
            name = entity.name,
            title = entity.title ?: "",
            type = entity.type,
            description = entity.description?.trim()?.ifEmpty { null },
            requiresApproval = entity.requiresApproval,
            role = role,
            form = form,
            message = message,
            script = script,
            service = service,
            event = entity.event,
            inputJSON = toJSON(entity.input),
            outputJSON = toJSON(entity.output),
            recipient = entity.recipient?.let { recipient ->
                RecipientModel(
                    email = recipient.email,
                    displayName = recipient.displayName
                )
            }
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
            message = entity.messageId?.let { id -> MessageModel(id = id) },
            script = entity.messageId?.let { id -> ScriptModel(id = id) },
            event = entity.event,
        )
    }

    private fun toJSON(map: Map<String, Any>): String? {
        return if (map.isEmpty()) {
            null
        } else {
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map)
        }
    }
}
