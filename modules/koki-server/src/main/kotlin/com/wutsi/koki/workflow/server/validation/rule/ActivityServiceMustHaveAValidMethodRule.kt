package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityServiceMustHaveAValidMethodRule : AbstractActivityRule() {
    companion object {
        private val METHODS = listOf("GET", "POST", "DELETE", "PU")
    }

    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.activities
            .filter { activity -> activity.type == ActivityType.SERVICE }
            .filter { activity -> !METHODS.contains(activity.method?.uppercase()) }
            .map { activity -> createError(activity) }
    }
}
