package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

class ActivityNameMustHavelLessThan100CharactersRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val activities = workflow.activities.filter { activity -> activity.type == ActivityType.STOP }
        if (activities.isEmpty()){
            return listOf(createError(workflow))
        }
    }
}
