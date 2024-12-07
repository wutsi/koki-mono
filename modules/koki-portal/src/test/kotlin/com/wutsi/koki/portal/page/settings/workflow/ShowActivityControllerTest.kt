package com.wutsi.koki.portal.page.settings.workflow

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ShowActivityControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_ACTIVITY)
    }

    @Test
    fun form() {
        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .html(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.form")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun message() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.message")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE)
    }

    private fun generateFormHtml(): String {
        return getResourceAsString("/form-readonly.html")
    }
}
