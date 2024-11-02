package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

class WorkflowMustHaveAtLeastOneSTOPActivityRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val activities = workflow.activities.filter { activity -> activity.type == ActivityType.STOP }
        return if (activities.isEmpty()) {
            listOf(createError(workflow))
        } else {
            emptyList()
        }
    }
}
