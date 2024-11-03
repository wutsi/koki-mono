package com.wutsi.koki.workflow.dto

import jakarta.validation.constraints.Future
import java.util.Date

data class CreateWorkflowInstanceRequest(
    val workflowId: Long = -1,
    val participants: List<Participant> = emptyList(),
    val approverUserId: Long? = null,
    @get:Future val startAt: Date = Date(),
    @get:Future val dueAt: Date? = null,
    var parameters: Map<String, String> = emptyMap(),
)
