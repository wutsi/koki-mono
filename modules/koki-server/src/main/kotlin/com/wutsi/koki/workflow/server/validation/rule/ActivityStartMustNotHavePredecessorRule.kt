package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityStartMustNotHavePredecessorRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val activities = workflow.activities
            .filter { activity -> activity.type == ActivityType.START }
            .associateBy { activity -> activity.name }
        if (activities.isEmpty()) {
            return emptyList()
        }

        return workflow.flows
            .filter { flow -> activities.keys.contains(flow.to) }
            .map { flow -> createError(activities[flow.to]!!) }
    }
}
