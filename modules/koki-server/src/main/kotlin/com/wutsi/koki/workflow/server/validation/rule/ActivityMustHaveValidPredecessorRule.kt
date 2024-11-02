package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustHaveValidPredecessorRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val names = workflow.activities.map { activity -> activity.name }
        val result = mutableListOf<ValidationError>()
        workflow.activities
            .map { activity ->
                val invalidPredecessors = activity.predecessors
                    .filter { predecessor -> !names.contains(predecessor) }
                if (invalidPredecessors.isNotEmpty()) {
                    result.add(createError(activity, invalidPredecessors))
                }
            }
        return result
    }
}
