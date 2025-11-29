package com.wutsi.koki.lead.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.event.LeadCreatedEvent
import com.wutsi.koki.lead.server.dao.LeadRepository
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.server.dao.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/CreateLeadEndpoint.sql"])
class CreateLeadEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: LeadRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    private val request = CreateLeadRequest(
        listingId = 111L,
        message = "Hello",
        firstName = "Ray",
        lastName = "Sponsible",
        visitRequestedAt = Date(),
        phoneNumber = "+15147589999",
        email = "ray.sponsible@gmail.com",
        source = LeadSource.WEBSITE,
    )

    @Test
    fun create() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity("/v1/leads", request, CreateLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(request.listingId, lead.listing?.id)
        assertEquals(request.message, lead.message)
        assertEquals(request.firstName, lead.firstName)
        assertEquals(request.lastName, lead.lastName)
        assertEquals(df.format(request.visitRequestedAt), df.format(lead.visitRequestedAt))
        assertEquals(request.phoneNumber, lead.phoneNumber)
        assertEquals(request.email, lead.email)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(request.source, lead.source)

        val event = argumentCaptor<LeadCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.id, event.firstValue.leadId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)

        val user = userDao.findByEmailAndTenantId(request.email, lead.tenantId)
        assertEquals(user?.id, lead.userId)
        assertEquals(DEVICE_ID, user?.deviceId)
        assertEquals(TENANT_ID, user?.tenantId)
        assertEquals(lead.email, user?.email)
        assertEquals(lead.email, user?.username)
        assertEquals(lead.firstName + " " + lead.lastName, user?.displayName)
        assertEquals(false, user?.password?.isEmpty())
        assertEquals(lead.phoneNumber, user?.mobile)
    }

    @Test
    fun `create with existing user`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(email = "thomas.nkono@gmail.com"),
            CreateLeadResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(request.listingId, lead.listing?.id)
        assertEquals(request.message, lead.message)
        assertEquals(request.firstName, lead.firstName)
        assertEquals(request.lastName, lead.lastName)
        assertEquals(df.format(request.visitRequestedAt), df.format(lead.visitRequestedAt))
        assertEquals(request.phoneNumber, lead.phoneNumber)
        assertEquals("thomas.nkono@gmail.com", lead.email)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(request.source, lead.source)
        assertEquals(11L, lead.userId)

        val event = argumentCaptor<LeadCreatedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.id, event.firstValue.leadId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
    }
}
