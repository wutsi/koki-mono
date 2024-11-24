package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityEndMustNotHaveSuccessorRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val activities = workflow.activities
            .filter { activity -> activity.type == ActivityType.END }
            .associateBy { activity -> activity.name }
        if (activities.isEmpty()) {
            return emptyList()
        }

        return workflow.flows
            .filter { flow -> activities.keys.contains(flow.from) }
            .map { flow -> createError(activities[flow.from]!!) }
    }
}
