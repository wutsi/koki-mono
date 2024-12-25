package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.form.event.FormSubmittedEvent
import com.wutsi.koki.form.server.dao.FormDataRepository
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import com.wutsi.koki.workflow.server.engine.WorkflowEventListener
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/StartWorkflowInstanceEndpoint.sql"])
class StartWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var formDataDao: FormDataRepository

    @Autowired
    private lateinit var listener: WorkflowEventListener

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

        Thread.sleep(1000)
        val activityInstanceId = result.body?.activityInstanceId
        val activityInstance = activityInstanceDao.findById(activityInstanceId!!).get()
        assertEquals(WorkflowStatus.DONE, activityInstance.status)
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

    @Test
    fun `start on form subscription`() {
        val event = FormSubmittedEvent(
            tenantId = TENANT_ID,
            formId = "100",
            formDataId = "10011",
            userId = 100L
        )
        listener.onFormSubmitted(event)

        val instances = instanceDao.findByWorkflowId(500L)
        assertEquals(1, instances.size)
        assertEquals(WorkflowStatus.RUNNING, instances[0].status)
        assertEquals(event.userId, instances[0].createdById)

        val formData = formDataDao.findById(event.formDataId).get()
        assertEquals(instances[0].id, formData.workflowInstanceId)
        assertEquals(formData.data, instances[0].state)
    }

    @Test
    fun `do not start on form subscription when workflow is inactive`() {
        val event = FormSubmittedEvent(
            tenantId = TENANT_ID,
            formId = "110",
            formDataId = "11011",
            userId = 100L
        )
        listener.onFormSubmitted(event)

        val instances = instanceDao.findByWorkflowId(600L)
        assertEquals(0, instances.size)
    }

    @Test
    fun `do not start on form subscription when START is inactive`() {
        val event = FormSubmittedEvent(
            tenantId = TENANT_ID,
            formId = "120",
            formDataId = "12011",
            userId = 100L
        )
        listener.onFormSubmitted(event)

        val instances = instanceDao.findByWorkflowId(700L)
        assertEquals(0, instances.size)
    }
}
