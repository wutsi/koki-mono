package com.wutsi.koki.portal.payment.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.PaymentFixtures.transaction
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class PaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/payments/" + transaction.id)

        assertCurrentPageIs(PageName.PAYMENT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/payments/" + transaction.id)
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission payment`() {
        setUpUserWithoutPermissions(listOf("payment"))

        navigateTo("/payments/" + transaction.id)

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
