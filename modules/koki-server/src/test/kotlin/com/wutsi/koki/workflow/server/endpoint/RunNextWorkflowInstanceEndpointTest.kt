package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.RunNextWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.domain.ActivityInstanceEntity
import com.wutsi.koki.workflow.server.engine.WorkflowEngine
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/RunNextWorkflowInstanceEndpoint.sql"])
class RunNextWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @MockBean
    private lateinit var engine: WorkflowEngine

    @Test
    fun run() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-01/run-next",
                emptyMap<String, String>(),
                RunNextWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstanceIds = result.body!!.activityInstanceIds
        assertEquals(1, activityInstanceIds.size)

        val activityInstance = activityInstanceDao.findById(activityInstanceIds[0]).get()
        assertEquals(WorkflowStatus.RUNNING, activityInstance.status)
        assertEquals(101L, activityInstance.activity.id)
        assertEquals(101L, activityInstance.assignee?.id)
        assertNull(activityInstance.approver)
        assertEquals(ApprovalStatus.UNKNOWN, activityInstance.approval)

        val act = argumentCaptor<ActivityInstanceEntity>()
        verify(engine).execute(act.capture())
        assertEquals(activityInstance.id, act.firstValue.id)

        val workflowInstance = instanceDao.findById("wi-100-01").get()
        val activityInstances = activityInstanceDao.findByInstance(workflowInstance)
        assertEquals(2, activityInstances.size)
    }

    @Test
    fun `spawn multiple activities`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-02/run-next",
                emptyMap<String, String>(),
                RunNextWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstanceIds = result.body!!.activityInstanceIds
        assertEquals(2, activityInstanceIds.size)

        val activityInstances = activityInstanceDao.findAllById(activityInstanceIds).sortedBy { it.activity.id }
        val activityInstance1 = activityInstances[0]
        assertEquals(WorkflowStatus.RUNNING, activityInstance1.status)
        assertEquals(102L, activityInstance1.activity.id)

        val activityInstance2 = activityInstances[1]
        assertEquals(WorkflowStatus.RUNNING, activityInstance2.status)
        assertEquals(103L, activityInstance2.activity.id)

        val act = argumentCaptor<ActivityInstanceEntity>()
        verify(engine, times(2)).execute(act.capture())
        assertEquals(activityInstance1.id, act.firstValue.id)
        assertEquals(activityInstance2.id, act.secondValue.id)

        val workflowInstance = instanceDao.findById("wi-100-02").get()
        assertEquals(4, activityInstanceDao.findByInstance(workflowInstance).size)
    }

    @Test
    fun `current activity is RUNNING - with past activities DONE`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-03/run-next",
                emptyMap<String, String>(),
                RunNextWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstanceIds = result.body!!.activityInstanceIds
        assertTrue(activityInstanceIds.isEmpty())
    }

    @Test
    fun `no activity is DONE`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-04/run-next",
                emptyMap<String, String>(),
                RunNextWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstanceIds = result.body!!.activityInstanceIds
        assertTrue(activityInstanceIds.isEmpty())
    }

    @Test
    fun `workflow is not RUNNING`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-05/run-next",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }

    @Test
    fun `do not run inactive activities`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-110-01/run-next",
                emptyMap<String, String>(),
                RunNextWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstanceIds = result.body!!.activityInstanceIds
        assertEquals(1, activityInstanceIds.size)

        val activityInstances = activityInstanceDao.findAllById(activityInstanceIds).sortedBy { it.activity.id }
        val activityInstance1 = activityInstances[0]
        assertEquals(WorkflowStatus.RUNNING, activityInstance1.status)
        assertEquals(112L, activityInstance1.activity.id)
        assertNull(activityInstance1.assignee?.id)
        assertNull(activityInstance1.approver)
        assertEquals(ApprovalStatus.UNKNOWN, activityInstance1.approval)

        val act = argumentCaptor<ActivityInstanceEntity>()
        verify(engine).execute(act.capture())
        assertEquals(activityInstance1.id, act.firstValue.id)

        val workflowInstance = instanceDao.findById("wi-110-01").get()
        assertEquals(3, activityInstanceDao.findByInstance(workflowInstance).size)
    }

    @Test
    fun `workflow instance not found`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/xxXXXxx/run-next",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `workflow instance of another tenant`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-200-01/run-next",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_NOT_FOUND, result.body?.error?.code)
    }
}
