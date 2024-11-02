package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class WorkflowMustHaveOneStartActivityRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val activities = workflow.activities.filter { activity -> activity.type == ActivityType.START }
        return if (activities.size != 1) {
            listOf(createError(workflow))
        } else {
            emptyList()
        }
    }
}
