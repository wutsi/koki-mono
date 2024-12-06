package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
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
        assertEquals(true, workflow.requiresApprover)
        assertEquals(listOf("PARAM_1", "PARAM_2", "PARAM_3"), workflow.parameters)
        assertEquals(listOf(10L, 11L), workflow.roleIds)
        assertEquals(10L, workflow.approverRoleId)
        assertEquals(11, workflow.workflowInstanceCount)

        assertEquals(5, workflow.activities.size)
        val activities = workflow.activities.sortedBy { it.id }
        assertEquals(110L, activities[0].id)
        assertEquals("START", activities[0].name)
        assertEquals(ActivityType.START, activities[0].type)
        assertEquals("Start the process", activities[0].description)
        assertEquals(mapOf("a" to "p1", "b" to "p2"), activities[0].tags)
        assertTrue(activities[0].requiresApproval)
        assertNull(activities[0].roleId)

        assertEquals(111L, activities[1].id)
        assertEquals("WORKING", activities[1].name)
        assertEquals(ActivityType.MANUAL, activities[1].type)
        assertEquals("fill the taxes", activities[1].description)
        assertTrue(activities[1].tags.isEmpty())
        assertFalse(activities[1].requiresApproval)
        assertEquals(11L, activities[1].roleId)

        assertEquals(112L, activities[2].id)
        assertEquals("SEND", activities[2].name)
        assertEquals(ActivityType.SEND, activities[2].type)
        assertNull(activities[2].description)
        assertTrue(activities[2].tags.isEmpty())
        assertFalse(activities[2].requiresApproval)
        assertEquals(10L, activities[2].roleId)

        assertEquals(113L, activities[3].id)
        assertEquals("SUBMIT", activities[3].name)
        assertEquals(ActivityType.SERVICE, activities[3].type)
        assertNull(activities[3].description)
        assertTrue(activities[3].tags.isEmpty())
        assertFalse(activities[3].requiresApproval)
        assertEquals(10L, activities[3].roleId)

        assertEquals(114L, activities[4].id)
        assertEquals("STOP", activities[4].name)
        assertEquals(ActivityType.END, activities[4].type)
        assertNull(activities[4].description)
        assertTrue(activities[4].tags.isEmpty())
        assertFalse(activities[4].requiresApproval)
        assertNull(activities[4].roleId)

        val flows = workflow.flows
        assertEquals(5, flows.size)

        assertEquals(activities[0].id, flows[0].fromId)
        assertEquals(activities[1].id, flows[0].toId)
        assertNull(flows[0].expression)

        assertEquals(activities[1].id, flows[1].fromId)
        assertEquals(activities[2].id, flows[1].toId)
        assertNull(flows[1].expression)

        assertEquals(activities[1].id, flows[2].fromId)
        assertEquals(activities[3].id, flows[2].toId)
        assertEquals("submit=true", flows[2].expression)

        assertEquals(activities[2].id, flows[3].fromId)
        assertEquals(activities[4].id, flows[3].toId)
        assertNull(flows[3].expression)

        assertEquals(activities[3].id, flows[4].fromId)
        assertEquals(activities[4].id, flows[4].toId)
        assertNull(flows[4].expression)
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

    @Test
    fun `get workflow having activity with malformed tag`() {
        val result = rest.getForEntity("/v1/workflows/300", GetWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflow = result.body!!.workflow

        assertEquals(1, workflow.activities.size)
        val activities = workflow.activities
        assertTrue(activities[0].tags.isEmpty())
    }
}