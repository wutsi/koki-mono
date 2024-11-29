package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityManualMustHaveARoleRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.type == ActivityType.MANUAL }
            .filter { activity -> activity.role.isNullOrEmpty() }
            .map { activity -> createError(activity) }
    }
}
