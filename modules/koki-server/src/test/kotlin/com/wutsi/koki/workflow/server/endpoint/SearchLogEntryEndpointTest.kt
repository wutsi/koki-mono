package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/SearchLogEntryEndpoint.sql"])
class SearchLogEntryEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity(
            "/v1/logs",
            SearchLogEntryResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val entries = result.body!!.logEntries
        assertEquals(6, entries.size)
    }

    @Test
    fun `by activities`() {
        val result = rest.getForEntity(
            "/v1/logs?activity-instance-id=wi-100-01-start-done",
            SearchLogEntryResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val entries = result.body!!.logEntries
        assertEquals(3, entries.size)
    }

    @Test
    fun `by workflow`() {
        val result = rest.getForEntity(
            "/v1/logs?workflow-instance-id=wi-100-01",
            SearchLogEntryResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val entries = result.body!!.logEntries
        assertEquals(5, entries.size)
    }
}
