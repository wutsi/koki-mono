package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.WorkflowFixtures.workflowInstances
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.workflow.dto.SearchWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import java.util.UUID
import kotlin.test.Test

class WorkflowListControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/workflows")

        assertCurrentPageIs(PageName.WORKFLOW_LIST)
        assertElementCount("tr.workflow-instance", workflowInstances.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<WorkflowInstanceSummary>()
        repeat(20) {
            entries.add(workflowInstances[0].copy(id = UUID.randomUUID().toString()))
        }
        doReturn(SearchWorkflowInstanceResponse(entries))
            .doReturn(SearchWorkflowInstanceResponse(workflowInstances))
            .whenever(kokiWorkflowInstances)
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

        navigateTo("/workflows")

        assertCurrentPageIs(PageName.WORKFLOW_LIST)
        assertElementCount("tr.workflow-instance", entries.size)

        scrollToBottom()
        click("#workflow-instance-load-more a", 1000)
        assertElementCount("tr.workflow-instance", entries.size + workflowInstances.size)
    }
}
