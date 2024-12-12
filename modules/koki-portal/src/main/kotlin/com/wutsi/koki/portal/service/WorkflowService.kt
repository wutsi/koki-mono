package com.wutsi.koki.portal.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.ActivityModel
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.page.workflow.SaveWorkflowForm
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import com.wutsi.koki.workflow.dto.WorkflowSortBy
import org.springframework.stereotype.Service

@Service
class WorkflowService(
    private val koki: KokiWorkflow,
    private val mapper: WorkflowMapper,
    private val objectMapper: ObjectMapper,
    private val formService: FormService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val scriptService: ScriptService,
) {
    fun json(id: Long): String {
        return koki.json(id)
    }

    fun workflow(id: Long): WorkflowModel {
        val workflow = koki.workflow(id).workflow

        val roles = if (workflow.roleIds.isEmpty()) {
            emptyList()
        } else {
            userService.roles(ids = workflow.roleIds, limit = workflow.roleIds.size)
        }

        val approverRole = workflow.approverRoleId?.let { roleId ->
            roles.find { role -> role.id == roleId }
                ?: userService.role(roleId)
        }

        val formIds = workflow.activities.mapNotNull { activity -> activity.formId }.toSet()
        val forms = formService.forms(
            ids = formIds.toList(),
            limit = formIds.size,
            workflowInstanceId = null,
            activityInstanceId = null,
        )

        val messageIds = workflow.activities.mapNotNull { activity -> activity.messageId }.toSet()
        val messages = messageService.messages(
            ids = messageIds.toList(),
            limit = messageIds.size,
        )

        val scriptIds = workflow.activities.mapNotNull { activity -> activity.scriptId }.toSet()
        val scripts = scriptService.scripts(
            ids = scriptIds.toList(),
            limit = scriptIds.size,
        )

        val imageUrl = koki.imageUrl(id)

        return mapper.toWorkflowModel(
            entity = workflow,
            approverRole = approverRole,
            roles = roles,
            forms = forms,
            messages = messages,
            scripts = scripts,
            imageUrl = imageUrl,
        )
    }

    fun imageUrl(id: Long): String {
        return koki.imageUrl(id)
    }

    fun activities(
        ids: List<Long> = emptyList(),
        workflowIds: List<Long> = emptyList(),
        roleIds: List<Long> = emptyList(),
        formIds: List<String> = emptyList(),
        messageIds: List<String> = emptyList(),
        type: ActivityType? = null,
        active: Boolean = true,
        limit: Int = 20,
        offset: Int = 0,
    ): List<ActivityModel> {
        val activities = koki.activities(
            ids = ids,
            workflowIds = workflowIds,
            roleIds = roleIds,
            formIds = formIds,
            messageIds = messageIds,
            type = type,
            active = active,
            limit = limit,
            offset = offset
        ).activities
        return activities.map { activity ->
            mapper.toActivityModel(activity)
        }
    }

    fun workflows(
        ids: List<Long> = emptyList(),
        active: Boolean? = true,
        activityRoleIds: List<Long> = emptyList(),
        approverRoleIds: List<Long> = emptyList(),
        minWorkflowInstanceCount: Long? = null,
        limit: Int = 20,
        offset: Int = 0,
        sortBy: WorkflowSortBy? = WorkflowSortBy.TITLE,
        ascending: Boolean = false,
    ): List<WorkflowModel> {
        val workflows = koki.workflows(
            ids = ids,
            active = active,
            activityRoleIds = activityRoleIds,
            approverRoleIds = approverRoleIds,
            minWorkflowInstanceCount = minWorkflowInstanceCount,
            limit = limit,
            offset = offset,
            sortBy = sortBy,
            ascending = ascending,
        ).workflows
        return workflows.map { workflow -> mapper.toWorkflowModel(workflow) }
    }

    fun create(form: SaveWorkflowForm): Long {
        return koki.import(
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }

    fun update(id: Long, form: SaveWorkflowForm): Long {
        return koki.import(
            id,
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }
}
