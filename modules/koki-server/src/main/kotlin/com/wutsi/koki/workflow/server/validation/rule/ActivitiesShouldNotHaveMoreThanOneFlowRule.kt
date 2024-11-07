package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError

class ActivitiesShouldNotHaveMoreThanOneFlowRule : AbstractFlowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.flows.groupBy { flow -> "${flow.from}-${flow.to}" }
            .filter { entry -> entry.value.size > 1 }
            .map { entry -> createError(entry.value[0]) }
    }
}
