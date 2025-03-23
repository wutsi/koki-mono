package com.wutsi.koki.portal.tax.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
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
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
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
}
