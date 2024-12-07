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

        assertElementPresent(".my-cases-widget")
        assertElementCount(".my-cases-widget table tr", workflowInstances.size + 1)
    }

    @Test
    fun empty() {
        doReturn(SearchWorkflowInstanceResponse()).whenever(kokiWorkflowInstance)
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
        assertElementNotPresent(".my-cases-widget table")
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/workflows/widgets/my-cases")
        assertElementNotPresent(".my-cases-widget table")
    }
}
