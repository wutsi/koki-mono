package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.service.ExpressionEvaluator
import com.wutsi.koki.workflow.server.validation.ValidationError
import org.springframework.expression.ParseException

class FlowExpressionMustBeValidRule(
    private val expressionEvaluator: ExpressionEvaluator
) : AbstractFlowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        return workflow.flows.mapNotNull { flow ->
            if (!flow.expression.isNullOrEmpty()) {
                try {
                    expressionEvaluator.evaluate(flow.expression!!, emptyMap())
                    null
                } catch (ex: ParseException) {
                    createError(flow, listOf("${ex.expressionString} - ${ex.message}"))
                }
            } else {
                null
            }
        }
    }
}
