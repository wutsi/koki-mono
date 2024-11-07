package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustNotBeOrphanRule : AbstractActivityRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val froms = workflow.flows.map { flow -> flow.from }
        val tos = workflow.flows.map { flow -> flow.to }
        return workflow.activities
            .filter { activity -> !froms.contains(activity.name) }
            .filter { activity -> !tos.contains(activity.name) }
            .map { activity -> createError(activity) }
    }
}
