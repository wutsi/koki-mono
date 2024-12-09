package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityScriptMustHaveAScriptRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.type == ActivityType.SCRIPT }
            .filter { activity -> activity.script.isNullOrEmpty() }
            .map { activity -> createError(activity) }
    }
}
