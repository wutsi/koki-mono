package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ActivityUserInstanceControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        val html = generateFormHtml()
        doReturn(html).whenever(kokiForms)
            .html(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `edit form`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-user-edit-form")
        assertCurrentPageIs(PageName.FORM)
    }

    @Test
    fun `toolbar not available when activity not running`() {
        // GIVEN
        val instance = activityInstance.copy(status = WorkflowStatus.NEW)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".btn-activity-user-edit-form")
    }

    @Test
    fun `toolbar not available for another assignee`() {
        // GIVEN
        val instance = activityInstance.copy(assigneeUserId = 55L)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".widget-toolbar")
    }

    @Test
    fun `preview form`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")
        click("a.form")

        // THEN
        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.FORM)
    }

    private fun generateFormHtml(): String {
        return getResourceAsString("/form-readonly.html")
    }
}
