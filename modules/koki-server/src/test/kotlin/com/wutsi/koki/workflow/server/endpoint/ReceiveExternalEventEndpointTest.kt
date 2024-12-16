package com.wutsi.koki.tenant.server.server.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.ReceiveExternalEventRequest
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/ReceiveExternalEventEndpoint.sql"])
class ReceiveExternalEventEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var workflowInstanceDao: WorkflowInstanceRepository

    private val request = ReceiveExternalEventRequest(
        name = "order-received",
        data = mapOf(
            "payment_id" to "1111",
            "payment_date" to "2024-12-01",
            "payment_gateway" to "STRIPE"
        )
    )

    @Test
    fun received() {
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-01/events",
            request,
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(5000) // Wait for the event to be consume

        val activity1 = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(WorkflowStatus.DONE, activity1.status)

        val activity2 = activityInstanceDao.findById("wi-100-01-waiting-running").get()
        assertEquals(WorkflowStatus.RUNNING, activity2.status)

        val workflow = workflowInstanceDao.findById("wi-100-01").get()
        val state = workflow.stateAsMap(ObjectMapper())
        assertEquals(request.data["payment_id"], state["transaction_id"])
    }

    @Test
    fun `event not supported`() {
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-02/events",
            request.copy(name = "unsupported"),
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(5000) // Wait for the event to be consume

        val activity1 = activityInstanceDao.findById("wi-100-02-working-running").get()
        assertEquals(WorkflowStatus.RUNNING, activity1.status)

        val activity2 = activityInstanceDao.findById("wi-100-02-waiting-running").get()
        assertEquals(WorkflowStatus.RUNNING, activity2.status)
    }

    @Test
    fun `no receive event running`() {
        val result = rest.postForEntity(
            "/v1/workflow-instances/wi-100-03/events",
            request,
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(5000) // Wait for the event to be consume

        val activities = activityInstanceDao.findByWorkflowInstanceId("wi-100-03")
        assertEquals(1, activities.size)
    }
}
