package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
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
import java.text.SimpleDateFormat
import java.util.TimeZone
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
    private lateinit var engine: WorkflowEngine

    @Test
    fun start() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result =
            rest.postForEntity("/v1/workflow-instances/wi-100/start", emptyMap<String, String>(), Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val instance = instanceDao.findById("wi-100").get()
        assertNotNull(instance.startedAt)
        assertEquals(WorkflowStatus.RUNNING, instance.status)

        val activities = activityInstanceDao.findByInstance(instance)
        assertEquals(1, activities.size)
        assertEquals(WorkflowStatus.RUNNING, activities[0].status)
        assertNotNull(activities[0].startedAt)
        assertNull(activities[0].assignee)

        val act = argumentCaptor<ActivityInstanceEntity>()
        verify(engine).execute(act.capture())
        assertEquals(activities[0].id, act.firstValue.id)
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
