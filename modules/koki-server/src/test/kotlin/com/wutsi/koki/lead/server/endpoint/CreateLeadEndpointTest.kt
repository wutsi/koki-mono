package com.wutsi.koki.lead.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.lead.dto.CreateLeadRequest
import com.wutsi.koki.lead.dto.CreateLeadResponse
import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import com.wutsi.koki.lead.server.dao.LeadMessageRepository
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
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/CreateLeadEndpoint.sql"])
class CreateLeadEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: LeadRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var messageDao: LeadMessageRepository

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
        source = LeadSource.LISTING,
        country = "CA",
        cityId = 333L,
    )

    @Test
    fun `create listing lead`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity("/v1/leads", request, CreateLeadResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(TENANT_ID, lead.tenantId)
        assertEquals(DEVICE_ID, lead.deviceId)
        assertEquals(request.listingId, lead.listing?.id)
        assertEquals(1111L, lead.agentUserId)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(request.source, lead.source)
        assertNotNull(lead.userId)

        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val user = userDao.findById(lead.userId).get()
        assertEquals(DEVICE_ID, user.deviceId)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.email, user.email)
        assertEquals("ray.sponsible", user.username)
        assertEquals(request.firstName + " " + request.lastName, user.displayName)
        assertEquals(false, user.password.isEmpty())
        assertEquals(request.phoneNumber, user.mobile)
        assertEquals(request.country?.lowercase(), user.country)
        assertEquals(request.cityId, request.cityId)

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(true, event.firstValue.newLead)
    }

    @Test
    fun `create listing lead with existing user`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(email = "thomas.nkono@gmail.com"),
            CreateLeadResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(TENANT_ID, lead.tenantId)
        assertEquals(DEVICE_ID, lead.deviceId)
        assertEquals(request.listingId, lead.listing?.id)
        assertEquals(1111L, lead.agentUserId)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(request.source, lead.source)
        assertEquals(11L, lead.userId)

        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(lead.id, message.lead.id)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(true, event.firstValue.newLead)
    }

    @Test
    fun `create listing lead with existing username`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(email = "omam.mbiyick@hotmail.com"),
            CreateLeadResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(TENANT_ID, lead.tenantId)
        assertEquals(DEVICE_ID, lead.deviceId)
        assertEquals(request.listingId, lead.listing?.id)
        assertEquals(1111L, lead.agentUserId)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(request.source, lead.source)
        assertNotNull(lead.userId)

        val user = userDao.findById(lead.userId).get()
        assertEquals(DEVICE_ID, user.deviceId)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals("omam.mbiyick@hotmail.com", user.email)
        assertEquals(true, user.username.startsWith("omam.mbiyick"))
        assertEquals(true, user.username.length > "omam.mbiyick".length)
        assertEquals(request.firstName + " " + request.lastName, user.displayName)
        assertEquals(false, user.password.isEmpty())
        assertEquals(request.phoneNumber, user.mobile)

        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(lead.id, message.lead.id)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(true, event.firstValue.newLead)
    }

    @Test
    fun `create listing lead with existing lead`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(userId = 12L, email = "roger.milla@gmail.com"),
            CreateLeadResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(lead.id, message.lead.id)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(false, event.firstValue.newLead)
    }

    @Test
    fun `create agent lead`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(
                listingId = null,
                agentUserId = 7777L,
                source = LeadSource.AGENT,
            ),
            CreateLeadResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        assertEquals(TENANT_ID, lead.tenantId)
        assertEquals(DEVICE_ID, lead.deviceId)
        assertEquals(null, lead.listing)
        assertEquals(7777L, lead.agentUserId)
        assertEquals(LeadStatus.NEW, lead.status)
        assertEquals(LeadSource.AGENT, lead.source)
        assertNotNull(lead.userId)

        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(lead.id, message.lead.id)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val user = userDao.findById(lead.userId).get()
        assertEquals(DEVICE_ID, user.deviceId)
        assertEquals(TENANT_ID, user.tenantId)
        assertEquals(request.email, user.email)
        assertEquals("ray.sponsible", user.username)
        assertEquals(request.firstName + " " + request.lastName, user.displayName)
        assertEquals(false, user.password.isEmpty())
        assertEquals(request.phoneNumber, user.mobile)
        assertEquals(request.country?.lowercase(), user.country)
        assertEquals(request.cityId, request.cityId)

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(true, event.firstValue.newLead)
    }

    @Test
    fun `create agent lead with existing lead`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(
                listingId = null,
                agentUserId = 7777,
                userId = 12L,
                email = "roger.milla@gmail.com"
            ),
            CreateLeadResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = dao.findById(response.body!!.leadId).get()
        val message = messageDao.findById(lead.lastMessage?.id ?: -1).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(request.message, message.content)
        assertEquals(df.format(request.visitRequestedAt), df.format(message.visitRequestedAt))

        val event = argumentCaptor<LeadMessageReceivedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(lead.lastMessage?.id, event.firstValue.messageId)
        assertEquals(lead.tenantId, event.firstValue.tenantId)
        assertEquals(false, event.firstValue.newLead)
    }

    @Test
    fun `create lead without listing not agent`() {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")

        val response = rest.postForEntity(
            "/v1/leads",
            request.copy(
                listingId = null,
                agentUserId = null,
            ),
            ErrorResponse::class.java,
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.LEAD_LISTING_OR_AGENT_MISSING, response.body?.error?.code)
    }
}
