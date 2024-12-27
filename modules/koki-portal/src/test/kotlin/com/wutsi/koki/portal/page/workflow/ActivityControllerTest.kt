package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import kotlin.test.Test

class ActivityControllerTest : AbstractPageControllerTest() {
    private fun setupActivity(type: ActivityType) {
        doReturn(
            GetActivityInstanceResponse(
                activityInstance.copy(
                    activity = activityInstance.activity.copy(type = type)
                )
            )
        ).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)
    }

    @Test
    fun user() {
        // GIVEN
        setupActivity(ActivityType.USER)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-complete-task")
        assertCurrentPageIs(PageName.TASK)
    }

    @Test
    fun manual() {
        // GIVEN
        setupActivity(ActivityType.MANUAL)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-complete-task")
        assertCurrentPageIs(PageName.TASK)
    }

    @Test
    fun receive() {
        // GIVEN
        setupActivity(ActivityType.RECEIVE)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }

    @Test
    fun send() {
        // GIVEN
        setupActivity(ActivityType.SEND)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }

    @Test
    fun service() {
        // GIVEN
        setupActivity(ActivityType.SERVICE)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }

    @Test
    fun script() {
        // GIVEN
        setupActivity(ActivityType.SCRIPT)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }

    @Test
    fun start() {
        // GIVEN
        setupActivity(ActivityType.START)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }

    @Test
    fun end() {
        // GIVEN
        setupActivity(ActivityType.END)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".btn-complete-task")
    }
}
