package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivitySendMustHaveARoleOrRecipientRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.type == ActivityType.SEND }
            .filter { activity -> activity.role.isNullOrEmpty() && activity.recipient == null }
            .map { activity -> createError(activity) }
    }
}
