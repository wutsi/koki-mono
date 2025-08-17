package com.wutsi.koki.tenant.server.service.email

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.email.server.service.EmailTemplateResolver
import com.wutsi.koki.email.server.service.Sender
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.server.command.SendPasswordCommand
import com.wutsi.koki.tenant.server.domain.PasswordResetTokenEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.PasswordResetTokenService
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class SendPasswordEmailWorkerTest {
    private val tokenService = mock<PasswordResetTokenService>()
    private val tenantService = mock<TenantService>()
    private val templateResolver = EmailTemplateResolver(
        templateEngine = MustacheTemplatingEngine(DefaultMustacheFactory())
    )
    private val sender = mock<Sender>()
    private val worker = SendPasswordEmailWorker(tokenService, tenantService, templateResolver, sender)

    val tenant = TenantEntity(
        id = 1L,
        portalUrl = "https://koki-portal.herokuapp.com"
    )

    val token = PasswordResetTokenEntity(
        id = "1111",
        tenantId = tenant.id!!,
        user = UserEntity(
            id = 111L,
            displayName = "Ray Sponsible",
            username = "ray.sponsible",
            email = "ray.sponsible@gmail.com",
            tenantId = tenant.id!!,
        )
    )

    @BeforeEach
    fun setUp() {
        doReturn(token).whenever(tokenService).get(any(), any())
        doReturn(tenant).whenever(tenantService).get(any())
    }

    @Test
    fun send() {
        val command = SendPasswordCommand(tokenId = token.id!!, tenantId = token.tenantId)
        val result = worker.notify(command)

        assertEquals(true, result)

        val body = """
            Dear Ray Sponsible,<br/><br/>

            Si vous avez demandé une réinitialisation de votre mot de passe, cliquez sur le bouton ci-dessous.<br/>
            Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer cet e-mail.<br/><br/>

            <a class="btn btn-primary" href="https://koki-portal.herokuapp.com/forgot/password/reset?token=1111">Réinitialiser votre mot de passe</a><br/><br/>

            <b>IMPORTANT:</b> Ce lien expire dans 24 heures.

        """.trimIndent()
        verify(sender).send(
            token.user,
            SendPasswordEmailWorker.SUBJECT,
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
