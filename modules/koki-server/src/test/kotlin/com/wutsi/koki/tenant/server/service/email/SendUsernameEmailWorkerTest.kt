package com.wutsi.koki.tenant.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.server.command.SendUsernameCommand
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class SendUsernameEmailWorkerTest {
    private val userService = mock<UserService>()
    private val tenantService = mock<TenantService>()
    private val templateResolver = EmailTemplateResolver(
        templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    )
    private val sender = mock<Sender>()
    private val worker = SendUsernameEmailWorker(userService, tenantService, templateResolver, sender)

    val tenant = TenantEntity(
        id = 1L,
        portalUrl = "https://koki-portal.herokuapp.com"
    )

    val user = UserEntity(
        id = 111L,
        displayName = "Ray Sponsible",
        username = "ray.sponsible",
        email = "ray.sponsible@gmail.com",
        tenantId = tenant.id!!,
    )

    @BeforeEach
    fun setUp() {
        doReturn(user).whenever(userService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
    }

    @Test
    fun send() {
        val command = SendUsernameCommand(userId = user.id!!, tenantId = user.tenantId)
        val result = worker.notify(command)

        assertEquals(true, result)

        val body = """
            Dear Ray Sponsible,<br/><br/>

            Comme vous l'avez demandé, voici le nom d'utilisateur de votre compte: <b>ray.sponsible</b>.<br/><br/>

            Cliquez sur le bouton ci-dessous pour vous connecter a votre compte:<br/><br/>
            <a class="btn btn-primary" href="https://koki-portal.herokuapp.com/login">Connectez-vous</a>

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
