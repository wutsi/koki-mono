package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.LogFixtures.logEntry
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class LogControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        // WHEN
        navigateTo("/workflows/logs/${logEntry.id}")

        // THEN
        assertCurrentPageIs(PageName.LOG)
    }
}
