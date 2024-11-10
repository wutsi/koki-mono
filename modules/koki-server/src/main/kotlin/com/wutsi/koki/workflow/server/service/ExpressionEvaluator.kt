package com.wutsi.koki.workflow.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.workflow.server.domain.FlowEntity
import com.wutsi.koki.workflow.server.domain.WorkflowInstanceEntity
import org.slf4j.LoggerFactory
import org.springframework.context.expression.MapAccessor
import org.springframework.expression.EvaluationException
import org.springframework.expression.ParseException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Service

@Service
class ExpressionEvaluator(private val objectMapper: ObjectMapper) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExpressionEvaluator::class.java)
    }

    @Throws(ParseException::class)
    fun evaluate(flow: FlowEntity, workflowInstance: WorkflowInstanceEntity): Boolean {
        if (flow.expression.isNullOrEmpty()) {
            return true
        }

        val data = mutableMapOf<String, String>()
        workflowInstance.parameters?.let { parameters ->
            data.putAll(objectMapper.readValue(parameters, Map::class.java) as Map<String, String>)
        }
        workflowInstance.state?.let { state ->
            data.putAll(objectMapper.readValue(state, Map::class.java) as Map<String, String>)
        }
        return evaluate(flow.expression!!, data)
    }

    @Throws(ParseException::class)
    fun evaluate(expression: String, data: Map<String, String>): Boolean {
        try {
            val parser = SpelExpressionParser()
            val xdata = convertPrimitive(data)
            val context = StandardEvaluationContext(xdata)
            context.addPropertyAccessor(MapAccessor())
            return parser.parseExpression(expression).getValue(context, Boolean::class.java)
        } catch (ex: EvaluationException) {
            LOGGER.warn("Evaluation failed", ex)
            return false
        }
    }

    private fun convertPrimitive(data: Map<String, String>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        data.forEach { entry ->
            if (isBoolean(entry.value)) {
                result[entry.key] = entry.value.toBoolean()
            } else if (isNumeric(entry.value)) {
                result[entry.key] = entry.value.toLong()
            } else if (isDecimal(entry.value)) {
                result[entry.key] = entry.value.toDouble()
            } else {
                result[entry.key] = entry.value
            }
        }
        return result
    }

    private fun isBoolean(value: String): Boolean {
        return value.equals("true", true) || value.equals("false", true)
    }

    private fun isDecimal(value: String): Boolean {
        return value.toDoubleOrNull() != null
    }

    private fun isNumeric(value: String): Boolean {
        return value.toLongOrNull() != null
    }
}
