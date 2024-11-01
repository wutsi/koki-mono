package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/GetWorkflowEndpoint.sql"])
class GetWorkflowEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/workflows/100", GetWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflow = result.body!!.workflow
        assertEquals("w1", workflow.name)
        assertEquals("description w1", workflow.description)
        assertEquals(false, workflow.active)

        assertEquals(5, workflow.activities.size)

        val activities = workflow.activities.sortedBy { it.id }
        assertEquals(110L, activities[0].id)
        assertEquals("START", activities[0].code)
        assertEquals(ActivityType.START, activities[0].type)
        assertEquals("starting...", activities[0].name)
        assertEquals("Start the process", activities[0].description)
        assertEquals(mapOf("a" to "p1", "b" to "p2"), activities[0].tags)
        assertTrue(activities[0].requiresApproval)
        assertTrue(activities[0].predecessors.isEmpty())

        assertEquals(111L, activities[1].id)
        assertEquals("WORKING", activities[1].code)
        assertEquals(ActivityType.MANUAL, activities[1].type)
        assertEquals("working...", activities[1].name)
        assertEquals("fill the taxes", activities[1].description)
        assertTrue(activities[1].tags.isEmpty())
        assertFalse(activities[1].requiresApproval)
        assertEquals(listOf("START"), activities[1].predecessors)

        assertEquals(112L, activities[2].id)
        assertEquals("SEND", activities[2].code)
        assertEquals(ActivityType.SEND, activities[2].type)
        assertEquals("sending...", activities[2].name)
        assertNull(activities[2].description)
        assertTrue(activities[2].tags.isEmpty())
        assertFalse(activities[2].requiresApproval)
        assertEquals(listOf("WORKING"), activities[2].predecessors)

        assertEquals(113L, activities[3].id)
        assertEquals("SUBMIT", activities[3].code)
        assertEquals(ActivityType.SERVICE, activities[3].type)
        assertEquals("submitting...", activities[3].name)
        assertNull(activities[3].description)
        assertTrue(activities[3].tags.isEmpty())
        assertFalse(activities[3].requiresApproval)
        assertEquals(listOf("WORKING"), activities[3].predecessors)

        assertEquals(114L, activities[4].id)
        assertEquals("STOP", activities[4].code)
        assertEquals(ActivityType.STOP, activities[4].type)
        assertEquals("done...", activities[4].name)
        assertNull(activities[4].description)
        assertTrue(activities[4].tags.isEmpty())
        assertFalse(activities[4].requiresApproval)
        assertEquals(listOf("SEND", "SUBMIT"), activities[4].predecessors)
    }

    @Test
    fun `get workflow not found`() {
        val result = rest.getForEntity("/v1/workflows/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get workflow of another tenant`() {
        val result = rest.getForEntity("/v1/workflows/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_NOT_FOUND, result.body?.error?.code)
    }
}
