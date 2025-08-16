package com.wutsi.koki.tenant.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.UserService
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class SendUsernameEmailWorkerTest {
    private val service = mock<UserService>()
    private val templateResolver = EmailTemplateResolver(
        templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    )
    private val sender = mock<Sender>()
    private val worker = SendUsernameEmailWorker(service, templateResolver, sender)

    @Test
    fun send() {
        val command = SendUsernameCommand(userId = 11L, tenantId = 1L)
        val user = UserEntity(
            id = command.userId,
            username = "ray.sponsible",
            email = "ray.sponsible@gmail.com",
            tenantId = command.tenantId,
        )
        doReturn(user).whenever(service).get(command.userId, command.tenantId)

        val result = worker.notify(command)

        assertEquals(true, result)

        val body = """
            Comme vous l'avez demandé, voici le nom d'utilisateur de votre compte.
            <ul>
                <li>Nom d'utilisateur: <b>${user.username}</b></li>
            </ul>

        """.trimIndent()
        verify(sender).send(
            user,
            SendUsernameEmailWorker.SUBJECT,
            body,
            emptyList(),
            command.tenantId
        )
    }

    @Test
    fun `unsupported command`() {
        val command = mapOf("foo" to "bar")
        val result = worker.notify(command)

        assertEquals(false, result)
    }
}
