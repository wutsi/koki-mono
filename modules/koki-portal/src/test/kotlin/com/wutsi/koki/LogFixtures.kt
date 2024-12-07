package com.wutsi.koki

import com.wutsi.koki.WorkflowFixtures.activityInstances
import com.wutsi.koki.WorkflowFixtures.workflowInstance
import com.wutsi.koki.workflow.dto.LogEntry
import com.wutsi.koki.workflow.dto.LogEntrySummary
import com.wutsi.koki.workflow.dto.LogEntryType

object LogFixtures {
    val logEntry = LogEntry(
        id = "1111",
        workflowInstanceId = workflowInstance.id,
        activityInstanceId = activityInstances[0].id,
        message = "Workfing...",
        type = LogEntryType.INFO,
        metadata = mapOf(
            "state" to mapOf(
                "foo" to "bar",
                "yo" to "man"
            )
        ),
        stackTrace = "Error at...\ncom...."
    )

    val logEntries = listOf(
        LogEntrySummary(
            id = "1111",
            workflowInstanceId = workflowInstance.id,
            activityInstanceId = activityInstances[0].id,
            message = "Workfing...",
            type = LogEntryType.INFO,
        ),
        LogEntrySummary(
            id = "1111",
            workflowInstanceId = workflowInstance.id,
            activityInstanceId = activityInstances[0].id,
            message = "Failed",
            type = LogEntryType.ERROR,
        ),
        LogEntrySummary(
            id = "1111",
            workflowInstanceId = workflowInstance.id,
            activityInstanceId = null,
            message = "Starting...",
            type = LogEntryType.INFO,
        ),
    )
}
