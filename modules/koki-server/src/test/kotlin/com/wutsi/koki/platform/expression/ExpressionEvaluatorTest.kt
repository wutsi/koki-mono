package com.wutsi.koki.platform.expression

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.springframework.expression.ParseException
import kotlin.test.Test
import kotlin.test.assertTrue

class ExpressionEvaluatorTest {
    private val evaluator = ExpressionEvaluator()

    private val state = mapOf(
        "WORK_TYPE" to "T1",
        "client_name" to "Ray Sponsible",
        "client_email" to "ray.sponsible@gmail.com",
        "submit" to "true",
        "amount" to "10000.0",
        "taxes" to 15.0,
        "client_id" to "5",
        "age" to 30,
        "new_client" to false,
        "status" to listOf("M", "F"),
    )

    @Test
    fun `evaluate from state`() {
        val expression = "client_email == 'ray.sponsible@gmail.com'"
        assertTrue(evaluator.evaluate(expression, state))
    }

    @Test
    fun `evaluate from parameter`() {
        val expression = "WORK_TYPE != 'T2'"
        assertTrue(evaluator.evaluate(expression, state))
    }

    @Test
    fun `evaluate decimal`() {
        val expression = "amount > 5000.0"
        assertTrue(evaluator.evaluate(expression, state))
    }

    @Test
    fun `evaluate numeric`() {
        val expression = "client_id > 1"
        assertTrue(evaluator.evaluate(expression, state))
    }

    @Test
    fun `evaluate boolean - true`() {
        val expression = "submit == true"
        assertTrue(evaluator.evaluate(expression, state))
    }

    @Test
    fun `evaluate boolean - false`() {
        val expression = "amount > 50000"
        assertFalse(evaluator.evaluate(expression, state))
    }

    @Test
    fun `malformed expression`() {
        val expression = "|\\"
        assertThrows<ParseException> { evaluator.evaluate(expression, state) }
    }

    @Test
    fun `unknown variable`() {
        val expression = "unknown=5"
        assertFalse(evaluator.evaluate(expression, state))
    }
}
