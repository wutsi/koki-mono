package com.wutsi.koki.workflow.server.validation

import com.wutsi.koki.workflow.dto.WorkflowData

interface ValidationRule {
    fun validate(workflow: WorkflowData): List<ValidationError>
}
