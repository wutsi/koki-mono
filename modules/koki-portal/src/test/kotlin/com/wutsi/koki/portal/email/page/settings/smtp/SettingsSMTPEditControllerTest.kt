package com.wutsi.koki.portal.email.page.settings.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.email.service.SMTPValidator
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.net.SocketException
import kotlin.collections.map
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.to

class SettingsSMTPEditControllerTest : AbstractPageControllerTest() {
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
            ResponseEntity(
                SearchConfigurationResponse(
                    config.map { cfg -> Configuration(name = cfg.key, value = cfg.value) }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }

    @Test
    fun save() {
        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementNotPresent(".alert-danger")

        verify(validator).validate("10.1.12.244", 555, "ray")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("10.1.12.244", request.firstValue.values[ConfigurationName.SMTP_HOST])
        assertEquals("555", request.firstValue.values[ConfigurationName.SMTP_PORT])
        assertEquals("ray", request.firstValue.values[ConfigurationName.SMTP_USERNAME])
        assertEquals("ray234", request.firstValue.values[ConfigurationName.SMTP_PASSWORD])
        assertEquals("no-reply@ray.com", request.firstValue.values[ConfigurationName.SMTP_FROM_ADDRESS])
        assertEquals("Ray Solutions", request.firstValue.values[ConfigurationName.SMTP_FROM_PERSONAL])

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `SMTP validation error`() {
        doThrow(SocketException::class).whenever(validator).validate(any(), any(), any())

        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementPresent(".alert-danger")

        verify(rest, never()).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            any(),
            eq(Any::class.java)
        )
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)
    }

    @Test
    fun back() {
        navigateTo("/settings/email/smtp/edit")

        click(".btn-back")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            any<SaveConfigurationRequest>(),
            eq(Any::class.java)
        )

        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)

        inputFields()
        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)
    }

    @Test
    fun `without permission email-admin`() {
        setUpUserWithoutPermissions(listOf("email:admin"))

        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    private fun inputFields() {
        input("[name=host]", "10.1.12.244")
        input("[name=port]", "555")
        input("[name=username]", "ray")
        input("[name=password]", "ray234")
        input("[name=fromAddress]", "no-reply@ray.com")
        input("[name=fromPersonal]", "Ray Solutions")
        click("button[type=submit]")
    }
}
