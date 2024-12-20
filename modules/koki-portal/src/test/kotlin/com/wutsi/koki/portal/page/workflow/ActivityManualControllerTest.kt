package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ActivityManualControllerTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            GetActivityInstanceResponse(
                activityInstance.copy(
                    activity = activityInstance.activity.copy(type = ActivityType.MANUAL)
                )
            )
        ).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)
    }

    @Test
    fun `show activity and complete`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()
        verify(kokiWorkflowInstances).complete(activityInstance.id, CompleteActivityInstanceRequest())

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY_COMPLETED)

        click(".btn-ok")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `show activity and do not complete`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.dismiss()

        verify(kokiWorkflowInstances, never()).complete(any(), any())

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".alert-danger")
    }

    @Test
    fun `show error`() {
        // GIVEN
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR)
        doThrow(ex).whenever(kokiWorkflowInstances).complete(any(), any())

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)

        click(".btn-activity-manual-complete")
        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `toolbar not available when activity not running`() {
        // GIVEN
        val instance = activityInstance.copy(status = WorkflowStatus.NEW)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".btn-activity-manual-complete")
    }

    @Test
    fun `toolbar not available for another assignee`() {
        // GIVEN
        val instance = activityInstance.copy(assigneeUserId = 55L)
        doReturn(GetActivityInstanceResponse(instance)).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)

        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")

        // THEN
        assertElementNotPresent(".widget-toolbar")
    }
}
