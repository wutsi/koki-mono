package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.expression.ExpressionEvaluator
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.springframework.stereotype.Service

@Service
class WorkflowExpressionEvaluator(
    private val objectMapper: ObjectMapper,
    private val delegate: ExpressionEvaluator,
) {
    fun evaluate(flow: FlowEntity, workflowInstance: WorkflowInstanceEntity): Boolean {
        val data = workflowInstance.stateAsMap(objectMapper)
        return evaluate(flow.expression, data)
    }

    fun evaluate(expression: String?, data: Map<String, Any>): Boolean {
        if (expression?.trim().isNullOrEmpty()) {
            return true
        } else {
            return delegate.evaluate(expression, data)
        }
    }
}
