package com.wutsi.koki.portal.page.settings.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.SMTPValidator
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.net.SocketException
import kotlin.test.Test
import kotlin.test.assertEquals

class EditSMTPControllerTest : AbstractPageControllerTest() {
    private val config = mapOf(
        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki",
    )

    @MockitoBean
    private lateinit var validator: SMTPValidator

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            SearchConfigurationResponse(
                config.map { cfg -> Configuration(name = cfg.key, value = cfg.value) }
            )
        ).whenever(kokiTenant).configurations(anyOrNull(), anyOrNull())
    }

    @Test
    fun save() {
        navigateTo("/settings/smtp/edit")
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementNotPresent(".alert-danger")

        verify(validator).validate("10.1.12.244", 555, "ray")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(kokiTenant).save(request.capture())

        assertEquals("10.1.12.244", request.firstValue.values[ConfigurationName.SMTP_HOST])
        assertEquals("555", request.firstValue.values[ConfigurationName.SMTP_PORT])
        assertEquals("ray", request.firstValue.values[ConfigurationName.SMTP_USERNAME])
        assertEquals("ray234", request.firstValue.values[ConfigurationName.SMTP_PASSWORD])
        assertEquals("no-reply@ray.com", request.firstValue.values[ConfigurationName.SMTP_FROM_ADDRESS])
        assertEquals("Ray Solutions", request.firstValue.values[ConfigurationName.SMTP_FROM_PERSONAL])

        assertCurrentPageIs(PageName.SETTINGS_SMTP_SAVED)
        click(".btn-ok")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun cancel() {
        navigateTo("/settings/smtp/edit")
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)

        scrollToBottom()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `SMTP validation error`() {
        doThrow(SocketException::class).whenever(validator).validate(any(), any(), any())

        navigateTo("/settings/smtp/edit")
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementPresent(".alert-danger")

        verify(kokiTenant, never()).save(any())
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)
    }

    @Test
    fun `backend error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(validator).validate(any(), any(), any())

        navigateTo("/settings/smtp/edit")
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementPresent(".alert-danger")

        verify(kokiTenant, never()).save(any())
        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)
    }

    private fun inputFields() {
        input("[name=host]", "10.1.12.244")
        input("[name=port]", "555")
        input("[name=username]", "ray")
        input("[name=password]", "ray234")
        input("[name=fromAddress]", "no-reply@ray.com")
        input("[name=fromPersonal]", "Ray Solutions")
        scrollToBottom()
        click("button[type=submit]")
    }
}
