package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustNotHaveSelfAsPredecessorRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.predecessors.contains(activity.name) }
            .map { activity -> createError(activity) }
    }
}
