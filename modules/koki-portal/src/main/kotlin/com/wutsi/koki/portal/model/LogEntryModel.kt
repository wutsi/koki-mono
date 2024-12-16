package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.LogEntryType
import java.util.Date

data class LogEntryModel(
    val id: String = "",
    val activityInstance: ActivityInstanceModel? = null,
    val workflowInstance: WorkflowInstanceModel? = null,
    val type: LogEntryType = LogEntryType.UNKNOWN,
    val message: String = "",
    val metadata: Map<String, Any> = emptyMap(),
    val metadataJSON: String? = null,
    val stackTrace: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
) {
    val url: String
        get() = "/workflows/logs/$id"
}
