package com.wutsi.koki.portal.tax.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `show - without permission tax-admin`() {
        setUpUserWithoutPermissions(listOf("tax:admin"))
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun back() {
        navigateTo("/settings/taxes")
        click(".btn-back")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `disable agent`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-agent-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "",
            request.firstValue.values[ConfigurationName.TAX_AI_AGENT_ENABLED]
        )
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `enable agent`() {
        disableConfig(ConfigurationName.TAX_AI_AGENT_ENABLED)

        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-agent-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(
            "1",
            request.firstValue.values[ConfigurationName.TAX_AI_AGENT_ENABLED]
        )
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification assignee - disable`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-assignee-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification assignee - enable`() {
        disableConfig(ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED)

        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-assignee-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification assignee - configure`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-assignee-configure")
        assertCurrentPageIs(PageName.TAX_SETTINGS_NOTIFICATION)

        input("#subject", "Yo man")
        inputCodeMirror("Hello world")
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED])
        assertEquals("Yo man", request.firstValue.values[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT])
        assertEquals("Hello world", request.firstValue.values[ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification document - disable`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-document-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification document - enable`() {
        disableConfig(ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED)

        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-document-enable")
        assertCurrentPageIs(PageName.TAX_SETTINGS_NOTIFICATION)

        input("#subject", "Yo man")
        inputCodeMirror("Hello world")
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_ENABLED])
        assertEquals("Yo man", request.firstValue.values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_SUBJECT])
        assertEquals("Hello world", request.firstValue.values[ConfigurationName.TAX_EMAIL_GATHERING_DOCUMENTS_BODY])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification document - configure`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-document-configure")
        assertCurrentPageIs(PageName.TAX_SETTINGS_NOTIFICATION)
    }

    @Test
    fun `email notification done - disable`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-done-disable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("", request.firstValue.values[ConfigurationName.TAX_EMAIL_DONE_ENABLED])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification done - enable`() {
        disableConfig(ConfigurationName.TAX_EMAIL_DONE_ENABLED)

        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-done-enable")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.TAX_EMAIL_DONE_ENABLED])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun `email notification done - configure`() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)

        click(".btn-done-configure")
        assertCurrentPageIs(PageName.TAX_SETTINGS_NOTIFICATION)

        input("#subject", "Yo man")
        inputCodeMirror("Hello world")
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.TAX_EMAIL_DONE_ENABLED])
        assertEquals("Yo man", request.firstValue.values[ConfigurationName.TAX_EMAIL_DONE_SUBJECT])
        assertEquals("Hello world", request.firstValue.values[ConfigurationName.TAX_EMAIL_DONE_BODY])
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }
}
