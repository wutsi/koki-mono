package com.wutsi.koki.portal.page.workflow.instance

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class WorkflowControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/workflows/${workflowInstance.id}")

        assertCurrentPageIs(PageName.WORKFLOW)
        assertElementCount("tr.activity", workflowInstance.activityInstances.size)

        Thread.sleep(1000)
    }

    @Test
    fun `open workflow`() {
        navigateTo("/workflows/${workflowInstance.id}")
        click("a.workflow")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW)
    }
}
