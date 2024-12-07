package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstances
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchActivityResponse
import com.wutsi.koki.workflow.dto.SetActivityInstanceAssigneeRequest
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class TaskQueueWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/workflows/widgets/task-queue")

        assertElementPresent(".task-queue-widget")
        assertElementCount(".task-queue-widget table tr", activityInstances.size + 1)

        click(".btn-assign")

        val request = argumentCaptor<SetActivityInstanceAssigneeRequest>()
        verify(kokiWorkflowInstance).assignee(request.capture())
        assertEquals(USER_ID, request.firstValue.userId)
        assertEquals(listOf(activityInstances[0].id), request.firstValue.activityInstanceIds)
    }

    @Test
    fun `no unassigned activity instance`() {
        doReturn(SearchActivityInstanceResponse()).whenever(kokiWorkflowInstance)
            .activities(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )

        navigateTo("/workflows/widgets/my-tasks")
        assertElementNotPresent(".my-tasks-widget table")
    }

    @Test
    fun `no activity associated with my role`() {
        doReturn(SearchActivityResponse()).whenever(kokiWorkflow)
            .activities(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
            )

        navigateTo("/workflows/widgets/my-tasks")
        assertElementNotPresent(".my-tasks-widget table")
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/task-queue")
        assertElementNotPresent(".task-queue-widget")
    }
}
