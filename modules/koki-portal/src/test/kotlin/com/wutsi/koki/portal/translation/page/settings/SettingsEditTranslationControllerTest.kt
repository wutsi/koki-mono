package com.wutsi.koki.portal.translation.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.platform.translation.TranslationProvider
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditTranslationControllerTest : AbstractPageControllerTest() {
    @Test
    fun ai() {
        navigateTo("/settings/translations/edit")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS_EDIT)

        select("#provider", 1)
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(1, request.firstValue.values.size)
        assertEquals(TranslationProvider.AI.name, request.firstValue.values[ConfigurationName.TRANSLATION_PROVIDER])
    }

    @Test
    fun aws() {
        navigateTo("/settings/translations/edit")
        assertCurrentPageIs(PageName.TRANSLATION_SETTINGS_EDIT)

        select("#provider", 2)
        input("#awsAccessKey", "11111")
        input("#awsSecretKey", "xxxxx")
        select("#awsRegion", 3)
        click("button[type=submit]")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(4, request.firstValue.values.size)
        assertEquals(TranslationProvider.AWS.name, request.firstValue.values[ConfigurationName.TRANSLATION_PROVIDER])
        assertEquals("ap-northeast-1", request.firstValue.values[ConfigurationName.TRANSLATION_PROVIDER_AWS_REGION])
        assertEquals("11111", request.firstValue.values[ConfigurationName.TRANSLATION_PROVIDER_AWS_ACCESS_KEY])
        assertEquals("xxxxx", request.firstValue.values[ConfigurationName.TRANSLATION_PROVIDER_AWS_SECRET_KEY])
    }

    @Test
    fun `edit - without permission translation-admin`() {
        setUpUserWithoutPermissions(listOf("translation:admin"))
        navigateTo("/settings/translations/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/translations/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }
}
