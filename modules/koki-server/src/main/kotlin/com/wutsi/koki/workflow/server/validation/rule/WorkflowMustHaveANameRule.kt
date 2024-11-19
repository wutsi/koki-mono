package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class WorkflowMustHaveANameRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return if (workflow.name.trim().isEmpty()) {
            listOf(createError(workflow))
        } else {
            emptyList()
        }
    }
}
