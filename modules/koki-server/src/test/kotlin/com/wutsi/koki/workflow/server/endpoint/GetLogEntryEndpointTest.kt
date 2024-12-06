package com.wutsi.koki.tenant.server.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.workflow.dto.GetLogEntryResponse
import com.wutsi.koki.workflow.dto.LogEntryType
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/workflow/GetLogEntryEndpoint.sql"])
class GetLogEntryEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/logs/100-001", GetLogEntryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val entry = result.body!!.logEntry
        assertEquals("wi-100-01-start-done", entry.activityInstanceId)
        assertEquals("wi-100-01", entry.workflowInstanceId)
        assertEquals("Starting", entry.message)
        assertEquals(LogEntryType.INFO, entry.type)
    }

    @Test
    fun `get log not found`() {
        val result = rest.getForEntity("/v1/logs/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.LOG_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `get log from another tenant`() {
        val result = rest.getForEntity("/v1/logs/200-001", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.LOG_NOT_FOUND, result.body?.error?.code)
    }
}
