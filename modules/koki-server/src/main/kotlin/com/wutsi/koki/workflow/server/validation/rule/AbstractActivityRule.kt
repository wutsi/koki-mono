package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

/**
 * Detect orphan activities. An orphan activity is not the predecessor of any node
 */
class OrphanActivityRule : ValidationRule {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val predecessors = workflow.activities.flatMap { activity -> activity.predecessors }.toSet()
        return workflow.activities
            .filter { activity -> activity.predecessors.isEmpty() && !predecessors.contains(activity.name) }
            .map { activity -> ValidationError("activity: ${activity.name}", "OrphanActivityRule") }
    }
}
