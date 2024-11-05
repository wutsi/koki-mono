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
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.engine.ActivityExecutor
import com.wutsi.koki.workflow.server.engine.ActivityExecutorProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/CompleteActivityInstanceEndpoint.sql"])
class CompleteActivityInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @MockBean
    private lateinit var activityExecutorProvider: ActivityExecutorProvider

    @BeforeTest
    override fun setUp() {
        super.setUp()

        val executor = mock(ActivityExecutor::class.java)
        doReturn(executor).whenever(activityExecutorProvider).get(any())
    }

    @Test
    fun complete() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-01/activities/wi-100-01-working-running/complete",
                emptyMap<String, String>(),
                Any::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(WorkflowStatus.DONE, activityInstance.status)
        assertNotNull(activityInstance.doneAt)
        assertNull(activityInstance.approver)
        assertEquals(ApprovalStatus.UNKNOWN, activityInstance.approval)

        verify(activityExecutorProvider, times(2)).get(any())

        val workflowInstance = instanceDao.findById("wi-100-01").get()
        val activityInstances = activityInstanceDao.findByInstance(workflowInstance)
        assertEquals(4, activityInstances.size)
    }

    @Test
    fun `complete an activity already DONE`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-02/activities/wi-100-02-working-done/complete",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `complete an activity of workflow not running`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-03/activities/wi-100-03-working-running/complete",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `start approval`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-110-01/activities/wi-110-01-working-running/complete",
                emptyMap<String, String>(),
                Any::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance = activityInstanceDao.findById("wi-110-01-working-running").get()
        assertEquals(WorkflowStatus.RUNNING, activityInstance.status)
        assertNull(activityInstance.doneAt)
        assertEquals(100L, activityInstance.approver?.id)
        assertEquals(ApprovalStatus.PENDING, activityInstance.approval)

        verify(activityExecutorProvider, never()).get(any())

        val workflowInstance = instanceDao.findById("wi-110-01").get()
        val activityInstances = activityInstanceDao.findByInstance(workflowInstance)
        assertEquals(2, activityInstances.size)
    }

    @Test
    fun `complete an activity under approval`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-110-02/activities/wi-110-02-working-running/complete",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_APPROVAL_PENDING, result.body?.error?.code)
    }
}
