package com.wutsi.koki.portal.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.page.workflow.CreateWorkflowForm
import com.wutsi.koki.portal.page.workflow.UpdateFormWorkflow
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import org.springframework.stereotype.Service

@Service
class WorkflowService(
    private val kokiWorkflow: KokiWorkflow,
    private val mapper: WorkflowMapper,
    private val objectMapper: ObjectMapper,
    private val formService: FormService,
    private val userService: UserService,
) {
    fun json(id: Long): String {
        return kokiWorkflow.getWorkflowJson(id)
    }

    fun workflow(id: Long): WorkflowModel {
        val workflow = kokiWorkflow.getWorkflow(id).workflow

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
        val imageUrl = kokiWorkflow.getWorkflowImageUrl(id)
        return mapper.toWorkflowModel(workflow, approverRole, roles, forms, imageUrl)
    }

    fun workflows(
        ids: List<Long> = emptyList(),
        limit: Int = 20,
        offset: Int = 0,
    ): List<WorkflowModel> {
        val workflows = kokiWorkflow.searchWorkflows(
            ids = ids,
            limit = limit,
            offset = offset,
        ).workflows
        return workflows.map { workflow -> mapper.toWorkflowModel(workflow) }
    }

    fun create(form: CreateWorkflowForm): Long {
        return kokiWorkflow.importWorkflow(
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }

    fun update(id: Long, form: UpdateFormWorkflow): Long {
        return kokiWorkflow.importWorkflow(
            id,
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }
}
