package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/StartWorkflowInstanceEndpoint.sql"])
class StartWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
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
    fun start() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100/start",
                emptyMap<String, String>(),
                StartWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflowInstance = instanceDao.findById("wi-100").get()
        assertEquals(fmt.format(Date()), fmt.format(workflowInstance.startedAt))
        assertEquals(WorkflowStatus.RUNNING, workflowInstance.status)

        verify(activityExecutorProvider).get(any())

        val activityInstanceId = result.body?.activityInstanceId
        assertNotNull(activityInstanceId)
        val activityInstance = activityInstanceDao.findById(activityInstanceId).get()
        assertEquals(WorkflowStatus.RUNNING, activityInstance.status)
        assertEquals(110L, activityInstance.activityId)
        assertEquals(fmt.format(Date()), fmt.format(activityInstance.startedAt))
        assertNull(activityInstance.assigneeId)
        assertNull(activityInstance.approverId)
        assertEquals(ApprovalStatus.UNKNOWN, activityInstance.approval)
    }

    @Test
    fun `cannot start inactive START`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-inactive-start/start",
                emptyMap<String, String>(),
                StartWorkflowInstanceResponse::class.java
            )

        val instance = instanceDao.findById("wi-inactive-start").get()
        assertEquals(WorkflowStatus.NEW, instance.status)

        val activityInstanceId = result.body?.activityInstanceId
        assertNull(activityInstanceId)

        verify(activityExecutorProvider, never()).get(any())
    }

    @Test
    fun `cannot start RUNNING instance`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-running/start",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `cannot start DONE instance`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-done/start",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }
}
