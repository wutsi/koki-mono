package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflow
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import kotlin.test.Test

class FormsWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun `show by workflow instance`() {
        navigateTo("/workflows/widgets/forms?workflow-instance-id=${workflowInstance.id}")

        assertElementPresent(".forms-widget")
        assertElementCount(".forms-widget table tr", 1)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun `show by workflow`() {
        navigateTo("/workflows/widgets/forms?workflow-id=${workflow.id}")

        assertElementPresent(".forms-widget")
        assertElementCount(".forms-widget table tr", 2)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun empty() {
        val xworkflow = workflow.copy(
            activities = workflow.activities.map { activity -> activity.copy(formId = null) }
        )
        doReturn(GetWorkflowResponse(xworkflow)).whenever(kokiWorkflow).workflow(workflow.id)

        doReturn(SearchFormResponse()).whenever(kokiForms)
            .forms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/workflows/widgets/forms?workflow-instance-id=${workflowInstance.id}")

        assertElementNotPresent(".forms-widget table")
        assertElementPresent(".empty-message")
    }
}
