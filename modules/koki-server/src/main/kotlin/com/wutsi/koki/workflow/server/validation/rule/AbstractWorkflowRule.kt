package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

abstract class AbstractWorkflowRule : AbstractRule() {
    protected fun createError(workflow: WorkflowData, values: List<String> = emptyList()): ValidationError =
        createError("workflow", workflow.name, values)
}
