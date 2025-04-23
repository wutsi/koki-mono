package com.wutsi.koki.portal.email.page.settings.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.email.service.SMTPValidator
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.net.SocketException
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsSMTPEditControllerTest : AbstractPageControllerTest() {
    @MockitoBean
    private lateinit var validator: SMTPValidator

    @Test
    fun external() {
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

        assertEquals("EXTERNAL", request.firstValue.values[ConfigurationName.SMTP_TYPE])
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
    fun koki() {
        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)

        inputFields(type = 1)

        verify(validator, never()).validate(any(), any(), any())

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("KOKI", request.firstValue.values[ConfigurationName.SMTP_TYPE])

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

    private fun inputFields(type: Int = 2) {
        select("[name=type]", type)
        if (type == 2) {
            input("[name=host]", "10.1.12.244")
            input("[name=port]", "555")
            input("[name=username]", "ray")
            input("[name=password]", "ray234")
            input("[name=fromAddress]", "no-reply@ray.com")
            input("[name=fromPersonal]", "Ray Solutions")
        }
        click("button[type=submit]")
    }
}
