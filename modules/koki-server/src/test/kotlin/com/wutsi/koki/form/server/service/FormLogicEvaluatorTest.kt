package com.wutsi.koki.form.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.dto.FormAction
import com.wutsi.koki.form.dto.FormLogic
import com.wutsi.koki.form.server.domain.FormDataEntity
import com.wutsi.koki.platform.expression.ExpressionEvaluator
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class FormLogicEvaluatorTest {
    val objectMapper = ObjectMapper()
    val delegate = mock<ExpressionEvaluator>()
    val evaluator = FormLogicEvaluator(objectMapper, delegate)

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
    private val formData = FormDataEntity(
        data = objectMapper.writeValueAsString(state)
    )
    private val logic = FormLogic(action = FormAction.HIDE, expression = "WORK_TYPE='T1'")

    @Test
    fun `successful evaluation`() {
        doReturn(true).whenever(delegate).evaluate(any(), any())

        val result = evaluator.evaluate(logic, formData)

        assertEquals(logic.action, result)
    }

    @Test
    fun `unsuccessful evaluation`() {
        doReturn(false).whenever(delegate).evaluate(any(), any())

        val result = evaluator.evaluate(logic, formData)

        assertEquals(FormAction.NONE, result)
    }

    @Test
    fun `empty expression`() {
        val result = evaluator.evaluate(logic.copy(expression = ""), formData)

        verify(delegate, never()).evaluate(any(), any())
        assertEquals(logic.action, result)
    }
}
