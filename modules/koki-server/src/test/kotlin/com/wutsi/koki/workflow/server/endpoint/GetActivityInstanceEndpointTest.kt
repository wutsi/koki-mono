package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/GetActivityInstanceEndpoint.sql"])
class GetActivityInstanceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result =
            rest.getForEntity("/v1/activity-instances/wi-100-01-start-done", GetActivityInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = result.body!!.activityInstance
        assertEquals(ApprovalStatus.PENDING, activityInstance.approval)
        assertEquals(WorkflowStatus.DONE, activityInstance.status)
        assertEquals(100L, activityInstance.assigneeUserId)
        assertEquals(101L, activityInstance.approverUserId)
        assertEquals("START", activityInstance.activity.name)
        assertEquals("w100", activityInstance.workflow.name)
    }

    @Test
    fun `get activity not found`() {
        val result = rest.getForEntity("/v1/activity-instances/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get activity from another tenant`() {
        val result = rest.getForEntity("/v1/activity-instances/wi-200-01-start-done", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NOT_FOUND, result.body?.error?.code)
    }
}
