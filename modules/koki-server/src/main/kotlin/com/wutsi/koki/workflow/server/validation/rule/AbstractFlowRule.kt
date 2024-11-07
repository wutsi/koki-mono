package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.server.validation.ValidationError

abstract class AbstractFlowRule : AbstractRule() {
    protected fun createError(flow: FlowData): ValidationError =
        createError("flow", "${flow.from} -> ${flow.to}")
}
