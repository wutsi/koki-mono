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

    @Suppress("UNCHECKED_CAST")
    @Throws(ParseException::class)
    fun evaluate(flow: FlowEntity, workflowInstance: WorkflowInstanceEntity): Boolean {
        if (flow.expression.isNullOrEmpty()) {
            return true
        }

        val data = mutableMapOf<String, Any>()
        workflowInstance.parameters?.let { parameters ->
            data.putAll(objectMapper.readValue(parameters, Map::class.java) as Map<String, String>)
        }
        workflowInstance.state?.let { state ->
            data.putAll(objectMapper.readValue(state, Map::class.java) as Map<String, String>)
        }
        return evaluate(flow.expression!!, data)
    }

    @Throws(ParseException::class)
    fun evaluate(expression: String, data: Map<String, Any>): Boolean {
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

    private fun convertPrimitive(data: Map<String, Any>): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        data.forEach { entry ->
            if (isBoolean(entry.value)) {
                result[entry.key] = entry.value.toString().toBoolean()
            } else if (isNumeric(entry.value)) {
                result[entry.key] = entry.value.toString().toLong()
            } else if (isDecimal(entry.value)) {
                result[entry.key] = entry.value.toString().toDouble()
            } else {
                result[entry.key] = entry.value
            }
        }
        return result
    }

    private fun isBoolean(value: Any): Boolean {
        return value is Boolean ||
            (value is String && (value.equals("true", true) || value.equals("false", true)))
    }

    private fun isDecimal(value: Any): Boolean {
        return value is Double || (value is String && value.toDoubleOrNull() != null)
    }

    private fun isNumeric(value: Any): Boolean {
        return value is Long || (value is String && value.toLongOrNull() != null)
    }
}
