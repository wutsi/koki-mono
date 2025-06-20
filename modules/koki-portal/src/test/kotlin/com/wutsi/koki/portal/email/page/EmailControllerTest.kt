package com.wutsi.koki.portal.email.page

import com.wutsi.koki.EmailFixtures.email
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test

class EmailControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show - without permission email`() {
        setupUserWithoutPermissions(listOf("email"))

        navigateTo("/emails/${email.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}
