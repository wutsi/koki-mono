package com.wutsi.koki.workflow.server.validation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.workflow.dto.WorkflowData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkflowValidatorTest {
    private val workflow = WorkflowData()

    private val rule1 = mock<ValidationRule> {}
    private val rule2 = mock<ValidationRule> {}
    private val rule3 = mock<ValidationRule> {}

    private val validator = WorkflowValidator(listOf(rule1, rule2, rule3))

    @Test
    fun success() {
        doReturn(emptyList<ValidationError>()).whenever(rule1).validate(workflow)
        doReturn(emptyList<ValidationError>()).whenever(rule2).validate(workflow)
        doReturn(emptyList<ValidationError>()).whenever(rule3).validate(workflow)

        val result = validator.validate(workflow)

        assertTrue(result.isEmpty())
    }

    @Test
    fun error() {
        val errorX = ValidationError("x", "XXX")
        val errorY = ValidationError("y", "YYY")
        val errorZ = ValidationError("z", "ZZZ")

        doReturn(listOf(errorX)).whenever(rule1).validate(workflow)
        doReturn(emptyList<ValidationError>()).whenever(rule2).validate(workflow)
        doReturn(listOf(errorY, errorZ)).whenever(rule3).validate(workflow)

        val result = validator.validate(workflow)

        assertEquals(3, result.size)
    }
}
