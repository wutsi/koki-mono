package com.wutsi.koki.platform.expression

import org.slf4j.LoggerFactory
import org.springframework.context.expression.MapAccessor
import org.springframework.expression.EvaluationException
import org.springframework.expression.ParseException
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Service

@Service
class ExpressionEvaluator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExpressionEvaluator::class.java)
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
