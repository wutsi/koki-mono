package com.wutsi.koki.portal.ai.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditAIControllerTest : AbstractPageControllerTest() {
    @Test
    fun koki() {
        disableAllConfigs()

        navigateTo("/settings/ai/edit")
        assertCurrentPageIs(PageName.AI_SETTINGS_EDIT)

        select("#provider", 1)
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(1, request.firstValue.values.size)
        assertEquals("KOKI", request.firstValue.values[ConfigurationName.AI_PROVIDER])
    }

    @Test
    fun gemini() {
        disableAllConfigs()

        navigateTo("/settings/ai/edit")
        assertCurrentPageIs(PageName.AI_SETTINGS_EDIT)

        select("#provider", 2)
        input("#geminiApiKey", "1111")
        select("#geminiModel", 2)
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(3, request.firstValue.values.size)
        assertEquals("GEMINI", request.firstValue.values[ConfigurationName.AI_PROVIDER])
        assertEquals("1111", request.firstValue.values[ConfigurationName.AI_PROVIDER_GEMINI_API_KEY])
        assertEquals("gemini-2.0-flash-lite", request.firstValue.values[ConfigurationName.AI_PROVIDER_GEMINI_MODEL])
    }

    @Test
    fun deepseek() {
        disableAllConfigs()

        navigateTo("/settings/ai/edit")
        assertCurrentPageIs(PageName.AI_SETTINGS_EDIT)

        select("#provider", 3)
        input("#deepseekApiKey", "1111")
        select("#deepseekModel", 1)
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(3, request.firstValue.values.size)
        assertEquals("DEEPSEEK", request.firstValue.values[ConfigurationName.AI_PROVIDER])
        assertEquals("1111", request.firstValue.values[ConfigurationName.AI_PROVIDER_DEEPSEEK_API_KEY])
        assertEquals("deepseek-chat", request.firstValue.values[ConfigurationName.AI_PROVIDER_DEEPSEEK_MODEL])
    }

    @Test
    fun `edit - without permission ai-admin`() {
        setUpUserWithoutPermissions(listOf("ai:admin"))
        navigateTo("/settings/ai/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/ai/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
