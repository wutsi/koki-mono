package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SearchActivityInstanceEndpoint.sql"])
class SearchActivityInstanceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/activity-instances", SearchActivityInstanceResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        assertEquals(12, activityInstances.size)
    }

    @Test
    fun `by assignee`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?assignee-id=100&assignee-id=101",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(3, activityInstanceIds.size)
        assertTrue(activityInstanceIds.contains("wi-100-03-working-running"))
        assertTrue(activityInstanceIds.contains("wi-100-05-start-running"))
        assertTrue(activityInstanceIds.contains("wi-100-06-submit-working"))
    }

    @Test
    fun `by approver`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?approver-id=102&approval=PENDING",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(1, activityInstanceIds.size)
        assertTrue(activityInstanceIds.contains("wi-100-03-working-running"))
    }

    @Test
    fun `by status`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?status=RUNNING",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(3, activityInstanceIds.size)
        assertTrue(activityInstanceIds.contains("wi-100-03-working-running"))
        assertTrue(activityInstanceIds.contains("wi-100-05-start-running"))
        assertTrue(activityInstanceIds.contains("wi-100-06-submit-working"))
    }

    @Test
    fun `by date`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?started-from=2020-01-10&started-to=2020-01-12",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(3, activityInstanceIds.size)
        assertTrue(activityInstanceIds.contains("wi-100-01-start-done"))
        assertTrue(activityInstanceIds.contains("wi-100-02-start-done"))
        assertTrue(activityInstanceIds.contains("wi-100-02-working-done"))
    }

    @Test
    fun `by ids`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?id=wi-100-01-start-done&id=wi-110-01-working-done",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(2, activityInstanceIds.size)
        assertTrue(activityInstanceIds.contains("wi-100-01-start-done"))
        assertTrue(activityInstanceIds.contains("wi-110-01-working-done"))
    }

    @Test
    fun `by workflow-instance-id`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?workflow-instance-id=wi-100-06",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(4, activityInstanceIds.size)
    }

    @Test
    fun `no assignee`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?assignee-id=-1",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(9, activityInstanceIds.size)
    }

    @Test
    fun `no approver`() {
        val result = rest.getForEntity(
            "/v1/activity-instances?approver-id=-1",
            SearchActivityInstanceResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val activityInstances = result.body!!.activityInstances
        val activityInstanceIds = activityInstances.map { activityInstanceId -> activityInstanceId.id }
        assertEquals(11, activityInstanceIds.size)
    }
}
