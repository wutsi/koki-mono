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
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.domain.MessageEntity
import com.wutsi.koki.message.server.service.MessageService
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.mapper.RoomMapper
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
        roomMapper = RoomMapper(),
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
        body = "This is an example of body... love it!",
        ownerType = ObjectType.ROOM,
        ownerId = 555L,
    )
    private val account = AccountEntity(
        id = 444L,
        tenantId = tenant.id!!,
        name = "Realtor Inc",
        email = "info@realtor.com"
    )
    private val image = FileEntity(
        id = 4309,
        tenantId = tenant.id!!,
        url = "https://picsum.photos/800/600",
    )
    private val room = RoomEntity(
        id = 777L,
        tenantId = tenant.id!!,
        accountId = account.id!!,
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
        assertEquals(account.id, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(false, request.firstValue.store)

        assertEquals(8, request.firstValue.data.size)
        assertEquals(message.senderName, request.firstValue.data["senderName"])
        assertEquals(message.senderEmail, request.firstValue.data["senderEmail"])
        assertEquals(message.senderPhone, request.firstValue.data["senderPhone"])
        assertEquals(
            "https://wa.me/23799500011?text=https%3A%2F%2Fwww.foo.com%2Frooms%2F777%2Fcozy-appartment%0A",
            request.firstValue.data["senderWhatsappUrl"]
        )
        assertEquals(message.body, request.firstValue.data["body"])
        assertEquals(room.title, request.firstValue.data["roomTitle"])
        assertEquals("${tenant.portalUrl}/rooms/${room.id}", request.firstValue.data["roomUrl"])
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
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
        assertEquals(
            """
                <table border="0" cellpadding="8" cellspacing="0" width="100%">
                    <tr>
                        <td align="center" colspan="2" valign="top">
                            <a href="https://clientX.koki.com/rooms/777">
                                <img alt="Cozy appartment" src="https://picsum.photos/800/600" style="max-width: 250px; max-height: 166px"/>
                                <div style="margin-top: 4px">Cozy appartment</div>
                            </a>
                        </td>
                        <td valign="top" width="100%">
                            <table border="0" cellpadding="8" cellspacing="0" width="100%">
                                <tr>
                                    <td>Ray Sponsible</td>
                                </tr>
                                <tr>
                                    <td style="border-bottom: 1px solid gray">
                                        This is an example of body... love it!
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <a href="https://wa.me/23799500011?text&#61;https%3A%2F%2Fwww.foo.com%2Frooms%2F777%2Fcozy-appartment" style="margin-right:20px">Reply on Whatsapp</a>

                                        <a href="mailto: ray.sponsible@gmail.com">Reply via Email</a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            """.trimIndent(),
            xbody,
        )
    }
}
