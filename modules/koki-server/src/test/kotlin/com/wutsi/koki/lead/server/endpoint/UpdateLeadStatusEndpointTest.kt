package com.wutsi.koki.lead.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.UpdateLeadStatusRequest
import com.wutsi.koki.lead.dto.event.LeadStatusChangedEvent
import com.wutsi.koki.lead.server.dao.LeadRepository
import com.wutsi.koki.platform.mq.Publisher
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/UpdateLeadStatusEndpoint.sql"])
class UpdateLeadStatusEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: LeadRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val request = UpdateLeadStatusRequest(
        status = LeadStatus.CONTACTED,
        nextVisitAt = DateUtils.addDays(Date(), 10),
        nextContactAt = DateUtils.addHours(DateUtils.addDays(Date(), 5), 3),
    )

    @Test
    fun status() {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val response = rest.postForEntity("/v1/leads/100/status", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(100L).get()
        assertEquals(request.status, lead.status)
        assertEquals(df.format(request.nextVisitAt), df.format(lead.nextVisitAt))
        assertEquals(df.format(request.nextContactAt), df.format(lead.nextContactAt))

        val event = argumentCaptor<LeadStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.id, event.firstValue.leadId)
        assertEquals(lead.status, event.firstValue.status)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
    }
}
