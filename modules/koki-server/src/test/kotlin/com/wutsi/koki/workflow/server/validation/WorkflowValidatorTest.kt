package com.wutsi.koki.workflow.server.validation

import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.FlowData
import com.wutsi.koki.workflow.dto.WorkflowData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkflowValidatorTest {
    @Autowired
    private lateinit var validator: WorkflowValidator

    @Test
    fun success() {
        val workflow = WorkflowData(
            name = "new",
            description = "This is a new workflow",
            activities = listOf(
                ActivityData(name = "start", type = ActivityType.START),
                ActivityData(name = "invoice", type = ActivityType.MANUAL),
                ActivityData(name = "stop", type = ActivityType.STOP),
            ),
            flows = listOf(
                FlowData(from = "start", to = "invoice"),
                FlowData(from = "invoice", to = "stop"),
            )
        )

        val result = validator.validate(workflow)

        assertTrue(result.isEmpty())
    }

    @Test
    fun error() {
        val workflow = WorkflowData(
            name = "new",
            description = "This is a new workflow",
            activities = listOf(
                ActivityData(name = "start", type = ActivityType.START),
                ActivityData(name = "invoice", type = ActivityType.MANUAL),
                ActivityData(name = "stop", type = ActivityType.STOP),
                ActivityData(name = "bad-precessor-1", type = ActivityType.RECEIVE),
                ActivityData(name = "bad-precessor-2", type = ActivityType.RECEIVE),
            ),
            flows = listOf(
                FlowData(from = "InVoice", to = "bad-precessor-1"),
                FlowData(from = "xxxx", to = "bad-precessor-2"),
            )
        )

        val result = validator.validate(workflow)

        assertFalse(result.isEmpty())
    }

    @Test
    fun `number of rules`() {
        assertEquals(10, validator.ruleCount())
    }
}
