package com.wutsi.koki.portal.page.workflow.widget

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.form.dto.FormSummary
import com.wutsi.koki.form.dto.SearchFormResponse
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.GetWorkflowInstanceResponse
import com.wutsi.koki.workflow.dto.GetWorkflowResponse
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowStatus
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class FormsWidgetControllerTest : AbstractPageControllerTest() {
    private val forms = listOf(
        FormSummary(
            id = "ef00493-403911",
            name = "FMR-001",
            title = "Incident Form",
        ),
        FormSummary(
            id = "ef00493-5553911",
            name = "FMR-002",
            title = "Approval Form",
        )
    )

    private val workflow = Workflow(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
        parameters = listOf("ORDER_ID"),
        active = true,
        requiresApprover = false,
        activities = listOf(
            Activity(
                id = 11L,
                type = ActivityType.START,
                name = "START",
                title = "Start"
            ),
            Activity(
                id = 12L,
                type = ActivityType.USER,
                name = "INPUT",
                title = "Input Data",
                description = "User input information about the case",
                active = true,
                requiresApproval = true,
                formId = forms[0].id,
            ),
            Activity(
                id = 13L,
                type = ActivityType.SERVICE,
                name = "INVOICE",
                title = "Generate the invoice",
                description = "Generate invoice using Service X",
                active = true,
                formId = forms[1].id,
            ),
            Activity(
                id = 13L,
                type = ActivityType.MANUAL,
                name = "PERFORM_TASK",
                title = "Perform the task",
                active = true,
            ),
            Activity(
                id = 99L,
                type = ActivityType.END,
                name = "STOP",
            ),
        ),
    )

    private val workflowInstance = WorkflowInstance(
        id = "xxx",
        workflowId = workflow.id,
        status = WorkflowStatus.RUNNING,
        approverUserId = 11L,
        createdAt = Date(),
        startAt = DateUtils.addDays(Date(), 3),
        startedAt = Date(),
        dueAt = DateUtils.addDays(Date(), 7),
        activityInstances = listOf(
            ActivityInstanceSummary(
                id = "111",
                activityId = workflow.activities[0].id,
                status = WorkflowStatus.DONE,
            ),
            ActivityInstanceSummary(
                id = "222",
                activityId = workflow.activities[1].id,
                status = WorkflowStatus.RUNNING,
            ),
            ActivityInstanceSummary(
                id = "333",
                activityId = workflow.activities[2].id,
                status = WorkflowStatus.RUNNING,
            )
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetWorkflowResponse(workflow)).whenever(kokiWorkflow).getWorkflow(workflow.id)

        doReturn(SearchFormResponse(forms)).whenever(kokiForms)
            .searchForms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetWorkflowInstanceResponse(workflowInstance))
            .whenever(kokiWorkflowInstance)
            .get(workflowInstance.id)
    }

    @Test
    fun `show by workflow instance`() {
        navigateTo("/workflows/widgets/forms?workflow-instance-id=${workflowInstance.id}")

        assertElementPresent(".forms-widget")
        assertElementCount(".forms-widget table tr", forms.size)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun `show by workflow`() {
        navigateTo("/workflows/widgets/forms?workflow-id=${workflow.id}")

        assertElementPresent(".forms-widget")
        assertElementCount(".forms-widget table tr", forms.size)
        assertElementNotPresent(".empty-message")
    }

    @Test
    fun empty() {
        val xworkflow = workflow.copy(
            activities = workflow.activities.map { activity -> activity.copy(formId = null) }
        )
        doReturn(GetWorkflowResponse(xworkflow)).whenever(kokiWorkflow).getWorkflow(workflow.id)

        doReturn(SearchFormResponse()).whenever(kokiForms)
            .searchForms(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        navigateTo("/workflows/widgets/forms?workflow-instance-id=${workflowInstance.id}")

        assertElementNotPresent(".forms-widget table")
        assertElementPresent(".empty-message")
    }
}
