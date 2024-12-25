package com.wutsi.koki.workflow.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/CompleteActivityInstanceEndpoint.sql"])
class CompleteActivityInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun complete() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = CompleteActivityInstanceRequest(
            state = mapOf(
                "A" to "aa",
                "B" to "bb",
                "C" to listOf("cc1", "cc2")
            )
        )
        val result =
            rest.postForEntity(
                "/v1/activity-instances/wi-100-01-working-running/complete",
                request,
                Any::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(WorkflowStatus.DONE, activityInstance.status)
        assertNotNull(activityInstance.doneAt)
        assertNull(activityInstance.approverId)
        assertEquals(ApprovalStatus.UNKNOWN, activityInstance.approval)
        assertEquals(fmt.format(Date()), fmt.format(activityInstance.doneAt))

        val workflowInstance = instanceDao.findById("wi-100-01").get()
        val state = objectMapper.readValue(workflowInstance.state, Map::class.java)
        assertEquals(3, state.size)
        assertEquals(request.state["A"], state["A"])
        assertEquals(request.state["B"], state["B"])
        assertEquals(request.state["C"], state["C"])

        Thread.sleep(1000)
        val activityInstances = activityInstanceDao.findByWorkflowInstanceId(workflowInstance.id!!)
        assertEquals(4, activityInstances.size)
    }

    @Test
    fun `complete an activity already DONE`() {
        val result =
            rest.postForEntity(
                "/v1/activity-instances/wi-100-02-working-done/complete",
                CompleteActivityInstanceRequest(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `complete an activity of workflow not running`() {
        val result =
            rest.postForEntity(
                "/v1/activity-instances/wi-100-03-working-running/complete",
                CompleteActivityInstanceRequest(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `start approval`() {
        val request = CompleteActivityInstanceRequest(
            state = mapOf(
                "A" to "aa",
                "B" to "bb",
                "C" to "",
                "D" to "dd",
            )
        )
        val result =
            rest.postForEntity(
                "/v1/activity-instances/wi-110-01-working-running/complete",
                request,
                Any::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-110-01-working-running").get()
        assertEquals(WorkflowStatus.RUNNING, activityInstance.status)
        assertNull(activityInstance.doneAt)
        assertEquals(100L, activityInstance.approverId)
        assertEquals(ApprovalStatus.PENDING, activityInstance.approval)

        val workflowInstance = instanceDao.findById("wi-110-01").get()
        val state = objectMapper.readValue(workflowInstance.state, Map::class.java)
        assertEquals(3, state.size)
        assertEquals(request.state["A"], state["A"])
        assertEquals(request.state["B"], state["B"])
        assertEquals(request.state["D"], state["D"])

        val activityInstances = activityInstanceDao.findByWorkflowInstanceId(workflowInstance.id!!)
        assertEquals(2, activityInstances.size)
    }

    @Test
    fun `complete an activity under approval`() {
        val result =
            rest.postForEntity(
                "/v1/activity-instances/wi-110-02-working-running/complete",
                CompleteActivityInstanceRequest(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_APPROVAL_PENDING, result.body?.error?.code)
    }
}
