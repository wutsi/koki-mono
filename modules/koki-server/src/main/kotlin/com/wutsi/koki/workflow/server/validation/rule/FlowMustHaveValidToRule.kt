package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class FlowMustHaveValidToRule : AbstractFlowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val names = workflow.activities.map { activity -> activity.name }
        val result = mutableListOf<ValidationError>()
        workflow.flows
            .map { flow ->
                if (!names.contains(flow.to)) {
                    result.add(createError(flow))
                }
            }
        return result
    }
}
