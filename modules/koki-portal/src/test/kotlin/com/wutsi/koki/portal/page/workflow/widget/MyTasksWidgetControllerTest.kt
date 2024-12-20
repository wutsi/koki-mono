package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.activityInstances
import com.wutsi.koki.workflow.dto.SearchActivityInstanceResponse
import kotlin.test.Test

class MyTasksWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/workflows/widgets/my-tasks")

        assertElementPresent(".my-tasks-widget")
        assertElementCount(".my-tasks-widget table tr", activityInstances.size + 1)
    }

    @Test
    fun empty() {
        doReturn(SearchActivityInstanceResponse()).whenever(kokiWorkflowInstances)
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
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/my-tasks")
        assertElementNotPresent(".my-tasks-widget")
    }
}
