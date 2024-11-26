package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.workflow.dto.SetActivityInstanceApproverRequest
import com.wutsi.koki.workflow.server.dao.ActivityInstanceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SetActivityInstanceApproverEndpoint.sql"])
class SetActivityInstanceApproverEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var activityInstanceDao: ActivityInstanceRepository

    @Test
    fun set() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val request = SetActivityInstanceApproverRequest(
            userId = 103L,
            activityInstanceIds = listOf(
                "wi-100-01-start-done",
                "wi-100-01-working-running"
            )
        )
        val result = rest.postForEntity(
            "/v1/activity-instances/approver",
            request,
            Any::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstance1 = activityInstanceDao.findById("wi-100-01-start-done").get()
        assertEquals(request.userId, activityInstance1.approverId)

        val activityInstance2 = activityInstanceDao.findById("wi-100-01-working-running").get()
        assertEquals(request.userId, activityInstance2.approverId)
    }
}
