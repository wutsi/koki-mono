package com.wutsi.koki.portal.email.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.EmailFixtures.email
import com.wutsi.koki.portal.common.page.PageName
import org.junit.jupiter.api.Test

class EmailControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show - without permission email`() {
        setUpUserWithoutPermissions(listOf("email"))

        navigateTo("/emails/${email.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}
