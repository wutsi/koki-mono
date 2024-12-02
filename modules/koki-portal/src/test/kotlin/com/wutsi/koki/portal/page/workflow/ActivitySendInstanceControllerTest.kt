package com.wutsi.koki.portal.page.workflow.instance

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.Message
import com.wutsi.koki.message.dto.MessageSummary
import com.wutsi.koki.message.dto.SearchMessageResponse
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Role
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SearchUserResponse
import com.wutsi.koki.tenant.dto.UserSummary
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.GetActivityInstanceResponse
import com.wutsi.koki.workflow.dto.SearchWorkflowResponse
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import java.util.Date
import kotlin.test.Test

class ActivitySendInstanceControllerTest : AbstractPageControllerTest() {
    private val roles = listOf(
        Role(id = 1L, name = "accountant", title = "Accountant"),
        Role(id = 2L, name = "hr", title = "Human Resource"),
        Role(id = 3L, name = "client", title = "Client"),
    )

    private val users = listOf(
        UserSummary(id = USER_ID, displayName = "Ray Sponsible"),
        UserSummary(id = 12L, displayName = "Roger Milla"),
    )

    private val workflow = WorkflowSummary(
        id = 1L,
        name = "WF-001",
        title = "Workflow #1",
        active = true,
        requiresApprover = true,
    )

    private val message = Message(
        id = "M-001",
        name = "M-001",
        subject = "Incident Form",
        body = "<p>Yo</p>"
    )

    private val activityInstance = ActivityInstance(
        id = "222",
        activity = Activity(
            id = 12L,
            type = ActivityType.SEND,
            name = "INPUT",
            title = "Input Data",
            description = "User input information about the case",
            roleId = roles[0].id,
            active = true,
            requiresApproval = true,
            messageId = message.id,
        ),
        workflowInstance = WorkflowInstanceSummary(
            id = "4304390-43094039",
            workflowId = workflow.id
        ),
        status = WorkflowStatus.RUNNING,
        assigneeUserId = USER_ID,
        approval = ApprovalStatus.UNKNOWN,
        createdAt = DateUtils.addDays(Date(), -10),
        startedAt = DateUtils.addDays(Date(), -5),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchRoleResponse(roles)).whenever(kokiUser)
            .searchRoles(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(SearchWorkflowResponse(listOf(workflow))).whenever(kokiWorkflow)
            .searchWorkflows(
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

        doReturn(SearchUserResponse(users)).whenever(kokiUser)
            .searchUsers(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetMessageResponse(message)).whenever(kokiMessages).get(any())
        doReturn(
            SearchMessageResponse(
                listOf(MessageSummary(id = message.id, name = message.name))
            )
        ).whenever(kokiMessages)
            .search(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(GetActivityInstanceResponse(activityInstance)).whenever(kokiWorkflowInstance)
            .activity(activityInstance.id)
    }

    @Test
    fun show() {
        navigateTo("/workflows/activities/${activityInstance.id}")

        assertCurrentPageIs(PageName.WORKFLOW_ACTIVITY)
        assertElementNotPresent(".widget-toolbar")
    }

    @Test
    fun `preview message`() {
        // WHEN
        navigateTo("/workflows/activities/${activityInstance.id}")
        click("a.message")

        // THEN
        val tabs = driver.getWindowHandles().toList()
        driver.switchTo().window(tabs[1])
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE)
    }
}
