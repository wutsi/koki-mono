package com.wutsi.koki.portal.payment.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsPaymentInteracControllerTest : AbstractPageControllerTest() {
    @Test
    fun `interac - disable`() {
        navigateTo("/settings/payments")
        click(".btn-interac-disable", 100)

        assertConfig("", ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED)
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `interac - enable`() {
        disableConfig(listOf(ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED))

        navigateTo("/settings/payments")
        click(".btn-interac-enable", 100)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_INTERAC)
        input("#email", "ray@gmail.com")
        input("#question", "Quel est la couleur du cheval blanc")
        input("#answer", "blanc")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED])
        assertEquals("ray@gmail.com", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL])
        assertEquals(
            "Quel est la couleur du cheval blanc",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION]
        )
        assertEquals("blanc", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `interac - configure`() {
        navigateTo("/settings/payments")
        click(".btn-interac-configure", 1000)

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS_INTERAC)
        input("#email", "ray@gmail.com")
        input("#question", "Quel est la couleur du cheval blanc")
        input("#answer", "blanc")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals("1", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED])
        assertEquals("ray@gmail.com", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL])
        assertEquals(
            "Quel est la couleur du cheval blanc",
            request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION]
        )
        assertEquals("blanc", request.firstValue.values[ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER])

        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }
}
