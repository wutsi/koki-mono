package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivitySendMustHaveAMessageRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.type == ActivityType.SEND }
            .filter { activity -> activity.message.isNullOrEmpty() }
            .map { activity -> createError(activity) }
    }
}
