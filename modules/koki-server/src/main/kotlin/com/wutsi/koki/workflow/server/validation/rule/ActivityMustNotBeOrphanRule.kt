package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustNotBeOrphanRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val predecessors = workflow.activities.flatMap { activity -> activity.predecessors }.toSet()
        return workflow.activities
            .filter { activity -> activity.predecessors.isEmpty() }
            .filter { activity -> !predecessors.contains(activity.name) }
            .map { activity -> createError(activity) }
    }
}
