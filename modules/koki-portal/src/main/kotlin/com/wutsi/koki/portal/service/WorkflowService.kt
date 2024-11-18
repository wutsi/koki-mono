package com.wutsi.koki.portal.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.portal.page.workflow.CreateWorkflowForm
import com.wutsi.koki.portal.page.workflow.UpdateFormWorkflow
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.workflow.dto.ImportWorkflowRequest
import org.springframework.stereotype.Service

@Service
class WorkflowService(
    private val kokiWorkflow: KokiWorkflow,
    private val kokiUser: KokiUser,
    private val mapper: WorkflowMapper,
    private val objectMapper: ObjectMapper,
) {
    fun workflow(id: Long): WorkflowModel {
        val workflow = kokiWorkflow.workflow(id).workflow
        val roles = if (workflow.roleIds.isEmpty()) {
            emptyList()
        } else {
            kokiUser.roles(workflow.roleIds).roles
        }
        val imageUrl = kokiWorkflow.imageUrl(id)
        return mapper.toWorkflowModel(workflow, roles, imageUrl)
    }

    fun workflows(
        limit: Int = 20,
        offset: Int = 0,
    ): List<WorkflowModel> {
        val workflows = kokiWorkflow.workflows(
            limit = limit,
            offset = offset,
        ).workflows
        return workflows.map { workflow -> mapper.toWorkflowModel(workflow) }
    }

    fun create(form: CreateWorkflowForm): Long {
        return kokiWorkflow.import(
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }

    fun update(id: Long, form: UpdateFormWorkflow): Long {
        return kokiWorkflow.import(
            id,
            request = objectMapper.readValue(form.json, ImportWorkflowRequest::class.java)
        ).workflowId
    }
}
