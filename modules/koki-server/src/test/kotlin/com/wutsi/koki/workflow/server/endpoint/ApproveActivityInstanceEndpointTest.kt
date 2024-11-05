package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceRequest
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ApprovalRepository
import com.wutsi.koki.workflow.server.engine.ActivityExecutor
import com.wutsi.koki.workflow.server.engine.ActivityExecutorProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ApproveActivityInstanceEndpoint.sql"])
class ApproveActivityInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var approvalRepository: ApprovalRepository

    @MockBean
    private lateinit var activityExecutorProvider: ActivityExecutorProvider

    @BeforeTest
    override fun setUp() {
        super.setUp()

        val executor = mock(ActivityExecutor::class.java)
        doReturn(executor).whenever(activityExecutorProvider).get(any())
    }

    @Test
    fun approve() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.APPROVED,
            comment = "Yo man",
            approverUserId = 100L
        )
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-01/activities/wi-100-01-working-running/approvals",
            request,
            ApproveActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(WorkflowStatus.DONE, activityInstance.status)
        assertNotNull(activityInstance.doneAt)
        assertEquals(request.status, activityInstance.approval)
        assertEquals(fmt.format(Date()), fmt.format(activityInstance.approvedAt))

        val approvalId = result.body!!.approvalId
        val approval = approvalRepository.findById(approvalId).get()
        assertEquals(request.status, approval.status)
        assertEquals(request.approverUserId, approval.approver.id)
        assertEquals(request.comment, approval.comment)
        assertEquals(activityInstance.approvedAt, approval.approvedAt)

        verify(activityExecutorProvider, times(2)).get(any())
    }

    @Test
    fun reject() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.REJECTED,
            comment = "Yo man",
            approverUserId = 100L
        )
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-01/activities/wi-100-01-working-running/approvals",
            request,
            ApproveActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(WorkflowStatus.RUNNING, activityInstance.status)
        assertNull(activityInstance.doneAt)
        assertEquals(request.status, activityInstance.approval)
        assertEquals(fmt.format(Date()), fmt.format(activityInstance.approvedAt))

        val approvalId = result.body!!.approvalId
        val approval = approvalRepository.findById(approvalId).get()
        assertEquals(request.status, approval.status)
        assertEquals(request.approverUserId, approval.approver.id)
        assertEquals(request.comment, approval.comment)
        assertEquals(activityInstance.approvedAt, approval.approvedAt)

        verify(activityExecutorProvider, never()).get(any())
    }

    @Test
    fun `approve an activity not RUNNING`() {
        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.REJECTED,
            comment = "Yo man",
            approverUserId = 100L
        )
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-03/activities/wi-100-03-working-done/approvals",
            request,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `approve a workflow not RUNNING`() {
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-04/activities/wi-100-04-working-running/approvals",
            emptyMap<String, String>(),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `approve an activity with approval not PENDING`() {
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-05/activities/wi-100-05-working-running/approvals",
            emptyMap<String, String>(),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NO_APPROVAL_PENDING, result.body?.error?.code)
    }
}