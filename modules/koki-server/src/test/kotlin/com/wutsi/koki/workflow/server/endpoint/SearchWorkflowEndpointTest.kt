package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SearchWorkflowEndpoint.sql"])
class SearchWorkflowEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/workflows", SearchWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflows
        assertEquals(4, workflows.size)

        assertEquals(100L, workflows[0].id)
        assertEquals("w100", workflows[0].name)
        assertEquals("This is the description", workflows[0].description)
        assertTrue(workflows[0].active)

        assertEquals(110L, workflows[1].id)
        assertEquals("w110", workflows[1].name)
        assertNull(workflows[1].description)
        assertTrue(workflows[1].active)

        assertEquals(120L, workflows[2].id)
        assertEquals("w120", workflows[2].name)
        assertNull(workflows[2].description)
        assertFalse(workflows[2].active)

        assertEquals(130L, workflows[3].id)
        assertEquals("w130", workflows[3].name)
        assertNull(workflows[3].description)
        assertFalse(workflows[3].active)
    }

    @Test
    fun active() {
        val result =
            rest.getForEntity("/v1/workflows?active=true&sort-by=ID&asc=false", SearchWorkflowResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflows
        assertEquals(2, workflows.size)

        assertEquals(110L, workflows[0].id)
        assertEquals(100L, workflows[1].id)
    }

    @Test
    fun ids() {
        val result = rest.getForEntity(
            "/v1/workflows?id=100&id=110&sort-by=NAME&asc=true",
            SearchWorkflowResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflows
        assertEquals(2, workflows.size)

        assertEquals(100L, workflows[0].id)
        assertEquals(110L, workflows[1].id)
    }

    @Test
    fun `exclude workflow from other tenant`() {
        val result = rest.getForEntity(
            "/v1/workflows?id=100&id=110&id=200&sort-by=NAME&asc=true",
            SearchWorkflowResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflows
        assertEquals(2, workflows.size)

        assertEquals(100L, workflows[0].id)
        assertEquals(110L, workflows[1].id)
    }
}
