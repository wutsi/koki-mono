package com.wutsi.koki.workflow.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.StartWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.dao.WorkflowInstanceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/StopWorkflowInstanceEndpoint.sql"])
class StopWorkflowInstanceEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var instanceDao: WorkflowInstanceRepository

    @Test
    fun stop() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-01/stop",
                emptyMap<String, String>(),
                StartWorkflowInstanceResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)

        val workflowInstance = instanceDao.findById("wi-100-01").get()
        assertEquals(fmt.format(Date()), fmt.format(workflowInstance.doneAt))
        assertEquals(WorkflowStatus.DONE, workflowInstance.status)
    }

    @Test
    fun `activities still running`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-02/stop",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_ACTIVITY_STILL_RUNNING, result.body?.error?.code)
    }

    @Test
    fun `workflow not running`() {
        val result =
            rest.postForEntity(
                "/v1/workflow-instances/wi-100-03/stop",
                emptyMap<String, String>(),
                ErrorResponse::class.java
            )

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR, result.body?.error?.code)
    }
}
