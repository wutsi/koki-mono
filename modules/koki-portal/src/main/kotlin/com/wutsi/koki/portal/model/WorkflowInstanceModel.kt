package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.WorkflowStatus
import java.util.Date

data class WorkflowInstanceModel(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val workflow: WorkflowModel = WorkflowModel(),
    val participants: List<ParticipantModel> = emptyList(),
    val approver: UserModel? = null,
    val status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val createdBy: UserModel? = null,
    val startAt: Date = Date(),
    val startedAt: Date? = null,
    val dueAt: Date? = null,
    val dueAtText: String? = null,
    val parameters: Map<String, String> = emptyMap(),
    val state: Map<String, Any> = emptyMap(),
    var activityInstances: List<ActivityInstanceModel> = emptyList(),
    val startAtText: String = "",
    val startedAtText: String? = null,
    val doneAt: Date? = null,
    val doneAtText: String? = null,
) {
    val url: String
        get() = "/workflows/$id"
}
