package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustHaveANameRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.name.trim().isEmpty() }
            .map { activity -> createError(activity) }
    }
}
