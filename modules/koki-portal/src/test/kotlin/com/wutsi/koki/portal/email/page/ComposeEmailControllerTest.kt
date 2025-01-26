package com.wutsi.koki.portal.email.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import org.junit.jupiter.api.Test

class ComposeEmailControllerTest : AbstractPageControllerTest() {
    @Test
    fun `compose - without permission email-send`() {
        setUpUserWithoutPermissions(listOf("email:send"))

        navigateTo("/emails/compose")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
