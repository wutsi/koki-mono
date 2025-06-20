package com.wutsi.koki.room.server.service.sender

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.email.server.service.filter.CssFilter
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.domain.MessageEntity
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.MessageEmailSender
import com.wutsi.koki.room.server.service.RoomService
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageEmailSenderTest {
    private val messageService = mock<MessageService>()
    private val roomService = mock<RoomService>()
    private val accountService = mock<AccountService>()
    private val emailService = mock<EmailService>()
    private val tenantService = mock<TenantService>()
    private val fileService = mock<FileService>()
    private val sender = MessageEmailSender(
        messageService = messageService,
        roomService = roomService,
        accountService = accountService,
        emailService = emailService,
        tenantService = tenantService,
        fileService = fileService,
    )

    private val tenant = TenantEntity(
        id = 111,
        portalUrl = "https://clientX.koki.com",
        clientPortalUrl = "https://www.foo.com"
    )
    private val message = MessageEntity(
        id = 333L,
        tenantId = tenant.id!!,
        senderEmail = "ray.sponsible@gmail.com",
        senderName = "Ray Sponsible",
        senderPhone = "+237 9 950 00 11",
        ownerType = ObjectType.ROOM,
        ownerId = 555L,
        body = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
            Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
        """.trimIndent(),
    )
    private val account = AccountEntity(
        id = 444L,
        tenantId = tenant.id!!,
        name = "Realtor Inc",
        email = "info@realtor.com",
        language = "fr"
    )
    private val image = FileEntity(
        id = 4309,
        tenantId = tenant.id!!,
        url = "https://picsum.photos/800/600",
    )
    private val room = RoomEntity(
        id = 777L,
        tenantId = tenant.id!!,
        account = account,
        title = "Cozy appartment",
        heroImageId = image.id,
        status = RoomStatus.PUBLISHED,
    )
    private val event = MessageSentEvent(
        messageId = message.id!!,
        tenantId = message.tenantId,
        owner = ObjectReference(id = room.id!!, type = ObjectType.ROOM)
    )

    @BeforeEach
    fun setUp() {
        doReturn(tenant).whenever(tenantService).get(tenant.id!!)
        doReturn(message).whenever(messageService).get(message.id!!, message.tenantId)
        doReturn(account).whenever(accountService).get(account.id!!, account.tenantId)
        doReturn(room).whenever(roomService).get(room.id!!, account.tenantId)
        doReturn(image).whenever(fileService).get(image.id!!, account.tenantId)
        doReturn(account).whenever(accountService).get(account.id!!, account.tenantId)
    }

    @Test
    fun send() {
        sender.send(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(message.tenantId))

        assertEquals(MessageEmailSender.SUBJECT, request.firstValue.subject)
        assertEquals(
            IOUtils.toString(this::class.java.getResourceAsStream("/room/email/message.html"), "utf-8"),
            request.firstValue.body,
        )
        assertEquals(account.email, request.firstValue.recipient.email)
        assertEquals(account.name, request.firstValue.recipient.displayName)
        assertEquals(account.language, request.firstValue.recipient.language)
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(false, request.firstValue.store)

        assertEquals(6, request.firstValue.data.size)
        assertEquals(message.senderName, request.firstValue.data["senderName"])
        assertEquals(message.body, request.firstValue.data["body"])
        assertEquals(room.title, request.firstValue.data["roomTitle"])
        assertEquals("${tenant.portalUrl}/rooms/${room.id}", request.firstValue.data["roomUrl"])
        assertEquals("${tenant.portalUrl}/rooms/${room.id}?tab=message", request.firstValue.data["messageUrl"])
        assertEquals(image.url, request.firstValue.data["heroImageUrl"])
    }

    @Test
    fun `owner not ROOM`() {
        sender.send(event.copy(owner = ObjectReference(id = 54054, type = ObjectType.ACCOUNT)))

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun email() {
        sender.send(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(message.tenantId))

        val body = IOUtils.toString(this::class.java.getResourceAsStream("/room/email/message.html"), "utf-8")
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = CssFilter().filter(
            template.apply(body, request.firstValue.data),
            event.tenantId,
        )

        println(xbody)
        assertEquals(
            """
                <html>
                  <head></head>
                  <body>
                    <table border="0" cellpadding="2" cellspacing="0" class="border" style="margin: 0 auto; max-width: 96%; ;border: 1px solid lightgray;">
                      <tbody>
                        <tr>
                          <td valign="top" width="100%">
                            <table border="0" cellpadding="8" cellspacing="0" width="100%">
                              <tbody>
                                <tr>
                                  <td width="1%">
                                    <b>From:</b>
                                  </td>
                                  <td>Ray Sponsible</td>
                                </tr>
                                <tr>
                                  <td>
                                    <b>Property:</b>
                                  </td>
                                  <td>
                                    <a href="https://clientX.koki.com/rooms/777">Cozy appartment</a>
                                  </td>
                                </tr>
                                <tr>
                                  <td class="text-center" colspan="2" style="text-align: center;">
                                    <a href="https://clientX.koki.com/rooms/777">
                                      <img alt="Cozy appartment" class="border" src="https://picsum.photos/800/600" style="max-width: 400px; max-height: 233px;border: 1px solid lightgray;">
                                    </a>
                                  </td>
                                </tr>
                                <tr>
                                  <td colspan="2">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</td>
                                </tr>
                                <tr>
                                  <td class="text-center" colspan="2" style="text-align: center;">
                                    <a class="btn btn-primary" href="https://clientX.koki.com/rooms/777?tab=message" style="border-radius: 4px;padding: 4px 8px;display: inline-block;font-weight: 400;text-align: center;vertical-align: middle;border: 1px solid transparent;font-size: 1rem;line-height: 1.5;text-decoration: none;;color: #FFFFFF;background-color: #1D7EDF;border: 1px solid transparent;">View Details</a>
                                  </td>
                                </tr>
                              </tbody>
                            </table>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </body>
                </html>
            """.trimIndent(),
            xbody,
        )
    }
}
