package com.wutsi.koki.workflow.server.io.validation

import com.wutsi.koki.workflow.dto.WorkflowData

interface ValidationRule {
    fun validate(workflow: WorkflowData): List<ValidationError>
}
