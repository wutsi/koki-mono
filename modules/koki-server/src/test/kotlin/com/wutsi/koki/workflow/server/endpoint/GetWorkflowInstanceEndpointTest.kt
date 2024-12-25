package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/GetWorkflowInstanceEndpoint.sql"])
class GetWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/workflow-instances/wi-100-01", GetWorkflowInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflowInstance = result.body!!.workflowInstance
        assertEquals(100L, workflowInstance.workflowId)
        assertEquals(101L, workflowInstance.approverUserId)
        assertEquals("2025", workflowInstance.title)
        assertEquals(WorkflowStatus.RUNNING, workflowInstance.status)
        assertEquals(
            mapOf(
                "customer_name" to "Ray Sponsible",
                "customer_email" to "ray.sponsible@gmail.com"
            ),
            workflowInstance.state,
        )
        assertEquals(
            listOf(
                Participant(userId = 100, roleId = 10),
                Participant(userId = 101, roleId = 11),
            ),
            workflowInstance.participants.sortedBy { it.userId },
        )

        assertEquals(2, workflowInstance.activityInstances.size)
    }

    @Test
    fun `get workflow not found`() {
        val result = rest.getForEntity("/v1/workflow-instances/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND, result.body?.error?.code)
    }
}
