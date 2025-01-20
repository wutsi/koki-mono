package com.wutsi.koki.portal.tax.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.taxes
import kotlin.test.Test

class TaxTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/taxes/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount(".tab-taxes tr.tax", taxes.size)
    }
}
