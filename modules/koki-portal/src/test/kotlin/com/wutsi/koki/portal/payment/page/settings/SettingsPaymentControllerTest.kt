package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsPaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.PAYMENT_SETTINGS)
    }

    @Test
    fun `show - without permission payment-admin`() {
        setUpUserWithoutPermissions(listOf("payment:admin"))
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `required login`() {
        setUpAnonymousUser()
        navigateTo("/settings/payments")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun back() {
        navigateTo("/settings/payments")
        click(".btn-back")
        assertCurrentPageIs(PageName.SETTINGS)
    }
}
