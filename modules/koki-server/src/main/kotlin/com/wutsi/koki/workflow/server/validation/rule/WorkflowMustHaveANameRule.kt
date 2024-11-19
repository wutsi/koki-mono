package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class WorkflowWithApprovalMustHaveApproverRoleRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val requiresApproval = workflow.activities.find { activity -> activity.requiresApproval } != null
        return if (requiresApproval && workflow.approverRole?.ifEmpty { null } == null) {
            listOf(createError(workflow))
        } else {
            emptyList()
        }
    }
}
