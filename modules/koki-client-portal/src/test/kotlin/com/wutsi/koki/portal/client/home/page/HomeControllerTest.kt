package com.wutsi.koki.portal.client.home.page

import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.common.page.PageName
import org.junit.jupiter.api.Test

class HomeControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/")
        assertCurrentPageIs(PageName.HOME)
    }
}
