package com.wutsi.koki.portal.client.error.page

import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.common.page.PageName
import kotlin.test.Test

class WutsiErrorControllerTest : AbstractPageControllerTest() {
    @Test
    fun `404`() {
        navigateTo("/xxxx")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `500`() {
        navigateTo("/fail")

        assertCurrentPageIs(PageName.ERROR_500)
    }
}
