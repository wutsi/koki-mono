package com.wutsi.koki.portal.rest

import com.wutsi.koki.portal.mapper.WorkflowMapper
import com.wutsi.koki.portal.model.WorkflowModel
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import org.springframework.stereotype.Service

@Service
class WorkflowService(
    private val kokiWorkflow: KokiWorkflow,
    private val kokiUser: KokiUser,
    private val mapper: WorkflowMapper,
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

    fun workflows(): List<WorkflowModel> {
        val workflows = kokiWorkflow.workflows().workflows
        return workflows.map { workflow -> mapper.toWorkflowModel(workflow) }
    }
}
