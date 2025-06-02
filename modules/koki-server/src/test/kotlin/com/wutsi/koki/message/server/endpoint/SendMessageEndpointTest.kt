package com.wutsi.koki.message.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.server.dao.AccountRepository
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.dao.MessageRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/message/SendMessageEndpoint.sql"])
class SendMessageEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    @Autowired
    private lateinit var accountDao: AccountRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun send() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Ray Sponsible",
            senderEmail = "Ray.Sponsible@gmail.com",
            senderPhone = "+15147580011",
            body = "Hello world",
            country = "ca",
            language = "fr",
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(request.owner?.id, message.ownerId)
        assertEquals(request.owner?.type, message.ownerType)
        assertEquals(request.senderName, message.senderName)
        assertEquals(request.senderEmail.lowercase(), message.senderEmail)
        assertEquals(request.senderPhone, message.senderPhone)
        assertEquals(MessageStatus.NEW, message.status)
        assertEquals(request.body, message.body)
        assertEquals(request.language, message.language)
        assertEquals(request.cityId, message.cityId)
        assertEquals(request.country?.uppercase(), message.country)
        assertNotNull(message.senderAccountId)

        val account = accountDao.findById(message.senderAccountId).get()
        assertEquals(request.senderName, account.name)
        assertEquals(request.senderEmail.lowercase(), account.email)
        assertEquals(request.senderPhone, account.mobile)
        assertEquals(request.language, account.language)
        assertEquals(request.cityId, account.shippingCityId)
        assertEquals(request.country?.uppercase(), account.shippingCountry)
        assertEquals(true, account.billingSameAsShippingAddress)

        val event = argumentCaptor<MessageSentEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(request.owner, event.firstValue.owner)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(message.id, event.firstValue.messageId)
    }

    @Test
    fun `send with cityId`() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Ray Sponsible",
            senderEmail = "Ray.Sponsible@gmail.com",
            senderPhone = "+15148880011",
            body = "Hello world",
            country = "ca",
            cityId = 2370101,
            language = "fr",
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(request.cityId, message.cityId)
        assertEquals("CM", message.country)

        val account = accountDao.findById(message.senderAccountId).get()
        assertEquals(request.cityId, account.shippingCityId)
        assertEquals("CM", account.shippingCountry)
    }

    @Test
    fun `send and update account`() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Ray Sponsible",
            senderEmail = "Info@inC.com",
            senderPhone = "+15147580011",
            body = "Hello world",
            country = "ca",
            cityId = null,
            language = "fr"
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(request.owner?.id, message.ownerId)
        assertEquals(request.owner?.type, message.ownerType)
        assertEquals(request.senderName, message.senderName)
        assertEquals(request.senderEmail.lowercase(), message.senderEmail)
        assertEquals(request.senderPhone, message.senderPhone)
        assertEquals(MessageStatus.NEW, message.status)
        assertEquals(request.body, message.body)
        assertEquals(request.country?.uppercase(), message.country)
        assertEquals(request.language, message.language)
        assertEquals(100L, message.senderAccountId)

        val account = accountDao.findById(message.senderAccountId).get()
        assertEquals("Inc", account.name)
        assertEquals(request.senderEmail.lowercase(), account.email)
        assertEquals(request.senderPhone, account.mobile)
        assertEquals(request.language, account.language)
        assertEquals(request.country?.uppercase(), account.shippingCountry)

        val event = argumentCaptor<MessageSentEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(request.owner, event.firstValue.owner)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(message.id, event.firstValue.messageId)
    }

    @Test
    fun `send and update account with cityId`() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Foo Bar",
            senderEmail = "Info@foobar.com",
            senderPhone = "+15147583333",
            body = "Hello world",
            country = "ca",
            cityId = 2370101L,
            language = "fr"
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(request.cityId, message.cityId)
        assertEquals("CM", message.country)

        val account = accountDao.findById(message.senderAccountId).get()
        assertEquals(request.cityId, account.shippingCityId)
        assertEquals("CM", account.shippingCountry)
    }

    @Test
    fun `send and never update account with mobile-country-city-language already set`() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Roger Milla",
            senderEmail = "info@yoman.com",
            senderPhone = "+15147586666",
            body = "Hello world",
            country = "ca",
            cityId = 2370101L,
            language = "en"
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(request.cityId, message.cityId)
        assertEquals("CM", message.country)

        val account = accountDao.findById(message.senderAccountId).get()
        assertEquals("YoMan", account.name)
        assertEquals(request.senderEmail.lowercase(), account.email)
        assertEquals("+18001111111", account.mobile)
        assertEquals("fr", account.language)
        assertEquals(2370102, account.shippingCityId)
        assertEquals("CM", account.shippingCountry)
    }
}
