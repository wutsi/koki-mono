package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError
import com.wutsi.koki.workflow.server.validation.ValidationRule

/**
 * Detect predecessor that do not match with any activity name
 */
class InvalidActivityPredecessorRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val names = workflow.activities.map { activity -> activity.name }
        val result = mutableListOf<ValidationError>()
        workflow.activities
            .map { activity ->
                activity.predecessors.forEach { predecessor ->
                    if (!names.contains(predecessor)) {
                        createError(activity, "Invalid predecessor: $predecessor")
                    }
                }
            }
        return result
    }
}
