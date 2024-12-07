package com.wutsi.koki

import com.wutsi.koki.FormFixtures.forms
import com.wutsi.koki.MessageFixtures.messages
import com.wutsi.koki.RoleFixtures.roles
import com.wutsi.koki.UserFixtures.USER_ID
import com.wutsi.koki.UserFixtures.users
import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ActivityInstanceSummary
import com.wutsi.koki.workflow.dto.ActivitySummary
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.Participant
import com.wutsi.koki.workflow.dto.Workflow
import com.wutsi.koki.workflow.dto.WorkflowInstance
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import com.wutsi.koki.workflow.server.domain.ActivityInstance
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object WorkflowFixtures {
    val WORFLOW_ID = 11L
    val WORKFLOW_INSTANCE_ID = "ccccc"
    val ACTVITY_INSTANCE_ID = "ffffff"

    val workflowPictureUrl = "https://picsum.photos/800/100"

    val workflow = Workflow(
        id = WORFLOW_ID,
        name = "WF-001",
        title = "Workflow #1",
        description = "This is an example of workflow",
        roleIds = roles.map { role -> role.id },
        parameters = listOf("PARAM_1", "PARAM_2"),
        active = true,
        requiresApprover = true,
        approverRoleId = 2L,
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
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
                formId = forms[0].id,
                messageId = messages[0].id,
            ),
            Activity(
                id = 13L,
                type = ActivityType.SERVICE,
                name = "INVOICE",
                title = "Generate the invoice",
                description = "Generate invoice using Service X",
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
            ),
            Activity(
                id = 13L,
                type = ActivityType.MANUAL,
                name = "PERFORM_TASK",
                title = "Perform the task",
                roleId = roles[0].id,
                active = true,
                requiresApproval = true,
                formId = forms[1].id,
                messageId = messages[1].id,
            ),
            Activity(
                id = 99L,
                type = ActivityType.END,
                name = "STOP",
            ),
        ),
    )

    val workflows = listOf(
        WorkflowSummary(
            id = WORFLOW_ID,
            name = "WF-001",
            title = "Workflow #1",
            active = true,
        ),
        WorkflowSummary(
            id = 2L,
            name = "WF-002",
            title = "Workflow #2",
            active = true,
        ),
        WorkflowSummary(
            id = 3L,
            name = "WF-003",
            title = "Workflow #3",
            active = false,
        ),
    )

    val activities = listOf(
        ActivitySummary(
            id = 11L,
            workflowId = workflows[0].id,
            type = ActivityType.START,
            name = "START",
            title = "Start"
        ),
        ActivitySummary(
            id = 12L,
            workflowId = workflows[1].id,
            type = ActivityType.USER,
            name = "INPUT",
            title = "Input Data",
        ),
        ActivitySummary(
            id = 13L,
            workflowId = workflows[1].id,
            type = ActivityType.MANUAL,
            name = "MANUAL",
            title = "Input Data",
        ),
    )

    val workflowInstance = WorkflowInstance(
        id = WORKFLOW_INSTANCE_ID,
        workflowId = workflow.id,
        title = "2024",
        status = WorkflowStatus.RUNNING,
        approverUserId = 11L,
        createdAt = Date(),
        startAt = DateUtils.addDays(Date(), 3),
        startedAt = Date(),
        dueAt = DateUtils.addDays(Date(), 7),
        participants = listOf(
            Participant(roleId = roles[0].id, userId = users[0].id),
            Participant(roleId = roles[1].id, userId = users[1].id),
            Participant(roleId = roles[2].id, userId = users[1].id),
        ),
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
                assigneeUserId = users[1].id,
                approval = ApprovalStatus.PENDING,
                approverUserId = 11L,
            )
        )
    )

    val workflowInstances = listOf(
        WorkflowInstanceSummary(
            id = WORKFLOW_INSTANCE_ID,
            workflowId = workflows[0].id,
            status = WorkflowStatus.RUNNING,
            approverUserId = users[0].id,
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        ),
        WorkflowInstanceSummary(
            id = "bbb",
            workflowId = workflows[1].id,
            status = WorkflowStatus.RUNNING,
            approverUserId = users[1].id,
            createdAt = Date(),
            startAt = DateUtils.addDays(Date(), 3),
            startedAt = Date(),
            dueAt = DateUtils.addDays(Date(), 7),
        ),
    )

    val activityInstance = ActivityInstance(
        id = ACTVITY_INSTANCE_ID,
        activity = Activity(
            id = 12L,
            type = ActivityType.USER,
            name = "INPUT",
            title = "Input Data",
            description = "User input information about the case",
            roleId = roles[0].id,
            active = true,
            requiresApproval = true,
            formId = forms[0].id,
            messageId = messages[0].id,
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

    val activityInstances = listOf(
        ActivityInstanceSummary(
            id = "1111",
            workflowInstanceId = workflowInstances[0].id,
            activityId = activities[0].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.PENDING,
            assigneeUserId = USER_ID,
            approverUserId = users[1].id,
        ),
        ActivityInstanceSummary(
            id = "222",
            workflowInstanceId = workflowInstances[0].id,
            activityId = activities[0].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.UNKNOWN,
            assigneeUserId = USER_ID,
            approverUserId = users[2].id,
        ),
        ActivityInstanceSummary(
            id = "333",
            workflowInstanceId = workflowInstances[1].id,
            activityId = activities[1].id,
            status = WorkflowStatus.RUNNING,
            approval = ApprovalStatus.UNKNOWN,
            assigneeUserId = USER_ID,
        ),
    )
}
