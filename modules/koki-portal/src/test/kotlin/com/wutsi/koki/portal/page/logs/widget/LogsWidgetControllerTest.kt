package com.wutsi.koki.portal.page.logs.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.LogFixtures
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.SearchLogEntryResponse
import kotlin.collections.toList
import kotlin.test.Test

class LogsWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show by workflow`() {
        navigateTo("/logs/widgets/list?workflow-instance-id=${workflowInstance.id}")

        assertElementPresent(".logs-widget")
        assertElementCount(".logs-widget table tr.log", LogFixtures.logEntries.size)
        assertElementNotPresent(".empty-message")

        click(".logs-widget .btn-view")
        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.LOG)
    }

    @Test
    fun `show by activity`() {
        navigateTo("/logs/widgets/list?activity-instance-id=${activityInstance.id}")

        assertElementPresent(".logs-widget")
        assertElementCount(".logs-widget table tr.log", LogFixtures.logEntries.size)
        assertElementNotPresent(".empty-message")

        click(".logs-widget .btn-view")
        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.LOG)
    }

    @Test
    fun empty() {
        doReturn(SearchLogEntryResponse()).whenever(kokiLogs)
            .logs(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/logs/widgets/list?workflow-instance-id=${workflowInstance.id}")

        assertElementNotPresent(".logs-widget table")
        assertElementPresent(".empty-message")
    }
}
