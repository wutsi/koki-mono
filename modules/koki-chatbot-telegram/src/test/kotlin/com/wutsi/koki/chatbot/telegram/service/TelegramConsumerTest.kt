package com.wutsi.koki.chatbot.telegram.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.chatbot.ai.agent.AgentFactory
import com.wutsi.koki.chatbot.ai.agent.SearchAgent
import com.wutsi.koki.chatbot.ai.data.PropertyData
import com.wutsi.koki.chatbot.ai.data.SearchAgentData
import com.wutsi.koki.chatbot.telegram.tenant.mapper.TenantMapper
import com.wutsi.koki.chatbot.telegram.tenant.service.TenantService
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.url.UrlShortener
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus
import jdk.jfr.internal.consumer.EventLog.update
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramConsumerTest {
    private val client = mock<TelegramClient>()
    private val kokiTenants = mock<KokiTenants>()
    private val tenantProvider = mock<TenantProvider>()
    private val tenantService = TenantService(koki = kokiTenants, mapper = TenantMapper())
    private val objectMapper = ObjectMapper()
    private val agentFactory = mock<AgentFactory>()
    private val urlShortener = mock<UrlShortener>()
    private val consumer = TelegramConsumer(
        client = client,
        agentFactory = agentFactory,
        tenantProvider = tenantProvider,
        tenantService = tenantService,
        objectMapper = objectMapper,
        executorService = Executors.newSingleThreadExecutor(),
        urlShortener = urlShortener,
    )

    private val agent = mock<SearchAgent>()
    private val tenantId = 111L
    private val tenant = Tenant(
        id = 1,
        name = "test",
        domainName = "localhost",
        locale = "CA",
        country = "CA",
        currency = "CAD",
        currencySymbol = "C\$",
        timeFormat = "hh:mm a",
        dateFormat = "yyyy-MM-dd",
        dateTimeFormat = "yyyy-MM-dd hh:mm a",
        numberFormat = "#,###,##0",
        monetaryFormat = "C\$ #,###,##0.00",
        status = TenantStatus.ACTIVE,
        logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
        iconUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
        websiteUrl = "https://www.google.com",
        portalUrl = "http://localhost:8080",
        clientPortalUrl = "https://localhost:8082",
    )
    val data = SearchAgentData(
        properties = listOf(
            PropertyData(
                url = "/rooms/1",
                pricePerMonth = 1550.0,
                currency = "CAD",
                bathrooms = 2,
                bedrooms = 5,
            ),
            PropertyData(
                url = "/rooms/2",
                pricePerNight = 550.0,
                currency = "CAD",
                bathrooms = 0,
                bedrooms = 1,
            )
        )
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenantId).whenever(tenantProvider).id()
        doReturn(GetTenantResponse(tenant)).whenever(kokiTenants).tenant(tenantId)
        doReturn(agent).whenever(agentFactory).crateSearchAgent()
        doReturn(objectMapper.writeValueAsString(data)).whenever(agent).run(any())
    }

    @Test
    fun bot() {
        consumer.consume(listOf(createUpdate("yo", bot = true)))
        Thread.sleep(1000)

        val msg = argumentCaptor<SendMessage>()
        verify(client).execute(msg.capture())

        assertEquals(TelegramConsumer.ANSWER_BOT, msg.firstValue.text)
    }

    @Test
    fun text() {
        consumer.consume(listOf(createUpdate("yo")))
        Thread.sleep(1000)

        val msg = argumentCaptor<SendMessage>()
        verify(client).execute(msg.capture())
        assertEquals(
            TelegramConsumer.ANSWER_HELP.trimIndent().replace("{{country}}", "Canada"),
            msg.firstValue.text.trimIndent(),
        )
    }

    @Test
    fun help() {
        consumer.consume(listOf(createUpdate("/help")))
        Thread.sleep(1000)

        val msg = argumentCaptor<SendMessage>()
        verify(client).execute(msg.capture())
        assertEquals(
            TelegramConsumer.ANSWER_HELP.trimIndent().replace("{{country}}", "Canada"),
            msg.firstValue.text.trimIndent(),
        )
    }

    @Test
    fun search() {
        val url1 = "https://bit.ly/1"
        val url2 = "https://bit.ly/2"
        doReturn(url1)
            .doReturn(url2)
            .whenever(urlShortener).shorten(any())

        val update = createUpdate("/search Im looking for apartment in Yaounde", language = "en")
        consumer.consume(listOf(update))
        Thread.sleep(1000)

        verify(agent).run("Im looking for apartment in Yaounde")

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(data.properties.size + 1)).execute(msg.capture())
        assertEquals(TelegramConsumer.ANSWER_SEARCHING, msg.firstValue.text)
        assertEquals(true, msg.secondValue.text.contains(url1))
        assertEquals(true, msg.thirdValue.text.contains(url2))

        verify(urlShortener).shorten("${tenant.clientPortalUrl}${data.properties[0].url}?lang=en&utm-medium=telegram")
        verify(urlShortener).shorten("${tenant.clientPortalUrl}${data.properties[1].url}?lang=en&utm-medium=telegram")
    }

    @Test
    fun empty() {
        doReturn("{}").whenever(agent).run(any())

        consumer.consume(listOf(createUpdate("/search Im looking for apartment in Yaounde")))
        Thread.sleep(1000)

        verify(agent).run("Im looking for apartment in Yaounde")

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(2)).execute(msg.capture())
        assertEquals(TelegramConsumer.ANSWER_SEARCHING, msg.firstValue.text)
        assertEquals(TelegramConsumer.ANSWER_NOT_FOUND, msg.secondValue.text)
    }

    @Test
    fun error() {
        doThrow(IllegalStateException::class).whenever(agent).run(any())

        consumer.consume(listOf(createUpdate("/search Im looking for apartment in Yaounde")))
        Thread.sleep(1000)

        val msg = argumentCaptor<SendMessage>()
        verify(client, times(2)).execute(msg.capture())
        assertEquals(TelegramConsumer.ANSWER_SEARCHING, msg.firstValue.text)
        assertEquals(TelegramConsumer.ANSWER_FAILURE, msg.secondValue.text)
    }

    private fun createUpdate(text: String, bot: Boolean = false, language: String = "en"): Update {
        val update = Update()
        update.message = Message()
        update.message.from = User(11L, "Ray Sponsible", bot)
        update.message.from.languageCode = language
        update.message.chat = Chat(123, "channel")
        update.message.text = text

        if (text.startsWith("/")) {
            val i = text.indexOf(" ")
            val cmd = if (i > 0) text.substring(0, i) else text
            val msg = MessageEntity("bot_command", 0, cmd.length)
            msg.text = cmd
            update.message.entities = listOf(msg)
        }
        return update
    }
}
