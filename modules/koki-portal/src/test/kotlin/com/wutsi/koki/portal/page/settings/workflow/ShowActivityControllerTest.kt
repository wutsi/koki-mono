package com.wutsi.koki.portal.page.settings.workflow

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
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.form")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_FORM)
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

    @Test
    fun script() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.script")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT)
    }

    @Test
    fun service() {
        navigateTo("/settings/workflows/${workflow.id}/activities/${workflow.activities[1].id}")
        click("a.service")

        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_SERVICE)
    }
}
