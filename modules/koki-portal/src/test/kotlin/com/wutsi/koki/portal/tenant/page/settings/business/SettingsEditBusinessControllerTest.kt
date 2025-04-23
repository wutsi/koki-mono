package com.wutsi.koki.portal.tenant.page.settings

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import org.mockito.ArgumentMatchers.eq
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditBusinessControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/tenant/business/edit")
        assertCurrentPageIs(PageName.TENANT_SETTINGS_BUSINESS_EDIT)

        input("#companyName", "Yo Man")
        input("#phone", "5147581111")
        input("#fax", "5147580100")
        input("#email", "info@yo-man.com")
        input("#website", "https://yo-man.com")
        scrollToBottom()
        input("#addressStreet", "101010 Mimboman")
        input("#addressPostalCode", "123456")
        click("button[type=submit]")

        val request = argumentCaptor<SaveBusinessRequest>()
        verify(rest).postForEntity(eq("$sdkBaseUrl/v1/businesses"), request.capture(), eq(Any::class.java))
        assertEquals("Yo Man", request.firstValue.companyName)
        assertEquals("5147581111", request.firstValue.phone)
        assertEquals("5147580100", request.firstValue.fax)
        assertEquals("info@yo-man.com", request.firstValue.email)
        assertEquals("https://yo-man.com", request.firstValue.website)
        assertEquals("123456", request.firstValue.addressPostalCode)
        assertEquals("101010 Mimboman", request.firstValue.addressStreet)
    }
}
