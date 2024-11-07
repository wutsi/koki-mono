package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivityMustNotHaveSelfAsPredecessorRule : AbstractFlowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.flows
            .filter { flow -> flow.from == flow.to }
            .map { flow -> createError(flow) }
    }
}
