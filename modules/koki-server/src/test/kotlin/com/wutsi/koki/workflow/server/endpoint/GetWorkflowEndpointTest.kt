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

        assertEquals(2, workflow.roles.size)
        val roles = workflow.roles
        assertEquals(10L, roles[0].id)
        assertEquals("accountant", roles[0].name)

        assertEquals(11L, roles[1].id)
        assertEquals("technician", roles[1].name)

        assertEquals(5, workflow.activities.size)
        val activities = workflow.activities.sortedBy { it.id }
        assertEquals(110L, activities[0].id)
        assertEquals("START", activities[0].name)
        assertEquals(ActivityType.START, activities[0].type)
        assertEquals("Start the process", activities[0].description)
        assertEquals(mapOf("a" to "p1", "b" to "p2"), activities[0].tags)
        assertTrue(activities[0].requiresApproval)
        assertTrue(activities[0].predecessorIds.isEmpty())
        assertNull(activities[0].roleId)

        assertEquals(111L, activities[1].id)
        assertEquals("WORKING", activities[1].name)
        assertEquals(ActivityType.MANUAL, activities[1].type)
        assertEquals("fill the taxes", activities[1].description)
        assertTrue(activities[1].tags.isEmpty())
        assertFalse(activities[1].requiresApproval)
        assertEquals(listOf(110L), activities[1].predecessorIds)
        assertEquals(11L, activities[1].roleId)

        assertEquals(112L, activities[2].id)
        assertEquals("SEND", activities[2].name)
        assertEquals(ActivityType.SEND, activities[2].type)
        assertNull(activities[2].description)
        assertTrue(activities[2].tags.isEmpty())
        assertFalse(activities[2].requiresApproval)
        assertEquals(listOf(111L), activities[2].predecessorIds)
        assertEquals(10L, activities[2].roleId)

        assertEquals(113L, activities[3].id)
        assertEquals("SUBMIT", activities[3].name)
        assertEquals(ActivityType.SERVICE, activities[3].type)
        assertNull(activities[3].description)
        assertTrue(activities[3].tags.isEmpty())
        assertFalse(activities[3].requiresApproval)
        assertEquals(listOf(111L), activities[3].predecessorIds)
        assertEquals(10L, activities[3].roleId)

        assertEquals(114L, activities[4].id)
        assertEquals("STOP", activities[4].name)
        assertEquals(ActivityType.STOP, activities[4].type)
        assertNull(activities[4].description)
        assertTrue(activities[4].tags.isEmpty())
        assertFalse(activities[4].requiresApproval)
        assertEquals(listOf(112L, 113L), activities[4].predecessorIds)
        assertNull(activities[4].roleId)
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
