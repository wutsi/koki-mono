package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.lead.dto.GetLeadResponse
import com.wutsi.koki.lead.dto.LeadStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/GetLeadEndpoint.sql"])
class GetLeadEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.getForEntity("/v1/leads/100", GetLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = response.body!!.lead
        assertEquals(111L, lead.listingId)
        assertEquals("xxx", lead.deviceId)
        assertEquals(11L, lead.userId)
        assertEquals(222L, lead.agentUserId)
        assertEquals(1000, lead.lastMessageId)
        assertEquals(LeadStatus.CONTACTED, lead.status)
        assertEquals("2026-12-30", df.format(lead.nextVisitAt))
        assertEquals("2026-11-30", df.format(lead.nextContactAt))
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/leads/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LEAD_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad tenant`() {
        val response = rest.getForEntity("/v1/leads/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.LEAD_NOT_FOUND, response.body?.error?.code)
    }
}
