package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.WorkflowFixtures.activityInstance
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.CompleteActivityInstanceRequest
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class TaskControllerManualTest : AbstractPageControllerTest() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        setUpActivity(activityInstance.copy(activity = activityInstance.activity.copy(type = ActivityType.MANUAL)))
    }

    @Test
    fun show() {
        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)
        assertElementNotPresent("#alert-error")
        assertElementNotPresent("#alert-done")
        assertElementNotPresent("#alert-not-assignee")
        assertElementPresent("#btn-complete")

        click("#pills-files-tab")
        waitForPresenceOf(".files-widget tr.file")
        assertElementCount(".files-widget tr.file", files.size)

        click("#pills-process-tab", 1000)
        assertElementPresent(".workflow-image img")

        click("#pills-task-tab", 1000)
        click("#btn-complete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiWorkflowInstances).complete(activityInstance.id, CompleteActivityInstanceRequest())

        assertCurrentPageIs(PageName.TASK_COMPLETED)
        click("#btn-ok")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun cancel() {
        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        click("#btn-complete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(kokiWorkflowInstances, never()).complete(any(), any())

        assertCurrentPageIs(PageName.TASK)
    }

    @Test
    fun error() {
        // Given
        val ex = createHttpClientErrorException(409, ErrorCode.WORKFLOW_INSTANCE_STATUS_ERROR)
        doThrow(ex).whenever(kokiWorkflowInstances).complete(any(), any())

        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)
        assertElementNotPresent("#alert-error")

        click("#btn-complete")
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertCurrentPageIs(PageName.TASK)
        assertElementPresent("#alert-error")
    }

    @Test
    fun `activity without assignee`() {
        // GIVEN
        setUpActivity(
            activityInstance.copy(
                activity = activityInstance.activity.copy(type = ActivityType.USER),
                assigneeUserId = null,
            )
        )

        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)
        assertElementPresent("#alert-not-assignee")
    }

    @Test
    fun `activity done`() {
        // GIVEN
        setUpActivity(
            activityInstance.copy(
                activity = activityInstance.activity.copy(type = ActivityType.USER),
                status = WorkflowStatus.DONE,
            )
        )

        // WHEN
        navigateTo("/tasks/${activityInstance.id}")

        // THEN
        assertCurrentPageIs(PageName.TASK)

        assertElementPresent("#alert-done")
    }

    private fun setUpActivity(activityInstance: ActivityInstance) {
        doReturn(GetActivityInstanceResponse(activityInstance)).whenever(kokiWorkflowInstances)
            .activity(activityInstance.id)
    }
}
