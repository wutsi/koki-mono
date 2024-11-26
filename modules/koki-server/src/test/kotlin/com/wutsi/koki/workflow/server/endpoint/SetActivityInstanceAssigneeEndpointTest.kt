package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.workflow.dto.SetActivityInstanceAssigneeRequest
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import com.wutsi.koki.workflow.server.dao.ParticipantRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SetActivityInstanceAssigneeEndpoint.sql"])
class SetActivityInstanceAssigneeEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Autowired
    private lateinit var participantDao: ParticipantRepository

    @Test
    fun set() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = SetActivityInstanceAssigneeRequest(
            userId = 103L,
            activityInstanceIds = listOf(
                "wi-100-01-working-running",
                "wi-100-01-send-running"
            )
        )
        val result = rest.postForEntity(
            "/v1/activity-instances/assignee",
            request,
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance1 = activityInstanceDao.findById("wi-100-01-send-running").get()
        assertEquals(request.userId, activityInstance1.assigneeId)

        val activityInstance2 = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(request.userId, activityInstance2.assigneeId)

        val partitipants = participantDao.findByWorkflowInstanceId(activityInstance1.workflowInstanceId)
        assertEquals(2, partitipants.size)
        assertEquals(request.userId, partitipants[0].userId)
        assertEquals(request.userId, partitipants[1].userId)
    }
}
