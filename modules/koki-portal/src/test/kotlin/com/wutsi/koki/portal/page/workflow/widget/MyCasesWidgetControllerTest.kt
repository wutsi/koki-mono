package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflowInstances
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import kotlin.test.Test

class MyCasesWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/workflows/widgets/my-cases")

        assertElementPresent(".workflow-instance-widget")
        assertElementCount(".workflow-instance-widget tr.workflow-instance", workflowInstances.size)
    }

    @Test
    fun empty() {
        doReturn(SearchWorkflowInstanceResponse()).whenever(kokiWorkflowInstances)
            .workflows(
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
            )

        navigateTo("/workflows/widgets/my-cases")
        assertElementNotPresent(".workflow-instance-widget table")
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/my-cases")
        assertElementNotPresent(".workflow-instance-widget table")
    }
}
