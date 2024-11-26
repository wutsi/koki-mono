package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceRequest
import com.wutsi.koki.workflow.dto.ApproveActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ApprovalRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.ActivityRunner
import com.wutsi.koki.workflow.server.engine.ActivityRunnerProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ApproveActivityInstanceEndpoint.sql"])
class ApproveActivityInstanceEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var approvalRepository: ApprovalRepository

    @MockitoBean
    private lateinit var activityExecutorProvider: ActivityRunnerProvider

    private val activityRunner = mock<ActivityRunner>()

    @BeforeTest
    override fun setUp() {
        super.setUp()

        doReturn(activityRunner).whenever(activityExecutorProvider).get(any())
    }

    @Test
    fun approve() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.APPROVED,
            comment = "Yo man",
        )
        val result = rest.postForEntity(
            "/v1/activity-instances/wi-100-01-working-running/approvals",
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
        assertEquals(USER_ID, approval.approverId)
        assertEquals(request.comment, approval.comment)
        assertEquals(activityInstance.approvedAt, approval.approvedAt)

        Thread.sleep(1000)
        val instance = argumentCaptor<ActivityInstanceEntity>()
        verify(activityRunner, times(2)).run(instance.capture(), any())
    }

    @Test
    fun reject() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.REJECTED,
            comment = "Yo man",
        )
        val result = rest.postForEntity(
            "/v1/activity-instances/wi-100-01-working-running/approvals",
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
        assertEquals(USER_ID, approval.approverId)
        assertEquals(request.comment, approval.comment)
        assertEquals(activityInstance.approvedAt, approval.approvedAt)

        Thread.sleep(1000)
        verify(activityExecutorProvider, never()).get(any())
    }

    @Test
    fun `approve an activity not RUNNING`() {
        val request = ApproveActivityInstanceRequest(
            status = ApprovalStatus.REJECTED,
            comment = "Yo man",
        )
        val result = rest.postForEntity(
            "/v1/activity-instances/wi-100-03-working-done/approvals",
            request,
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `approve a workflow not RUNNING`() {
        val result = rest.postForEntity(
            "/v1/activity-instances/wi-100-04-working-running/approvals",
            emptyMap<String, String>(),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `approve an activity with approval not PENDING`() {
        val result = rest.postForEntity(
            "/v1/activity-instances/wi-100-05-working-running/approvals",
            emptyMap<String, String>(),
            ErrorResponse::class.java
        )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_NO_APPROVAL_PENDING, result.body?.error?.code)
    }
}
