package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SearchWorkflowInstanceEndpoint.sql"])
class SearchWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/workflow-instances", SearchWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(8, workflows.size)
    }

    @Test
    fun participantUser() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?participant-user-id=100",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(2, workflows.size)

        assertEquals("wi-100-01", workflows[0].id)
        assertEquals("wi-100-02", workflows[1].id)
    }

    @Test
    fun participantRole() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?participant-role-id=10&participant-role-id=12",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(3, workflows.size)

        assertEquals("wi-100-01", workflows[0].id)
        assertEquals("wi-100-02", workflows[1].id)
        assertEquals("wi-100-03", workflows[2].id)
    }

    @Test
    fun creator() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?created-by-id=12",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(2, workflows.size)

        assertEquals("wi-100-06", workflows[0].id)
        assertEquals("wi-110-01", workflows[1].id)
    }

    @Test
    fun ids() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?id=wi-100-01&id=wi-100-03&id=wi-110-01&limit=2",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances.sortedBy { it.id }
        assertEquals(2, workflows.size)

        assertEquals("wi-100-01", workflows[0].id)
        assertEquals("wi-100-03", workflows[1].id)
    }

    @Test
    fun workflowIds() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?workflow-id=110&limit=2",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(1, workflows.size)

        assertEquals("wi-110-01", workflows[0].id)
    }

    @Test
    fun status() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?status=DONE",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(3, workflows.size)

        assertEquals("wi-100-01", workflows[0].id)
        assertEquals("wi-110-01", workflows[1].id)
        assertEquals("wi-120-01", workflows[2].id)
    }

    @Test
    fun startedAt() {
        val result = rest.getForEntity(
            "/v1/workflow-instances?start-from=2020-01-01&start-to=2020-01-31",
            SearchWorkflowInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflows = result.body!!.workflowInstances
        assertEquals(3, workflows.size)

        assertEquals("wi-100-02", workflows[0].id)
        assertEquals("wi-100-04", workflows[1].id)
        assertEquals("wi-100-05", workflows[2].id)
    }
}
