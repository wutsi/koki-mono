package com.wutsi.koki.portal.page.settings.workflow

import com.wutsi.koki.workflow.dto.Participant
import java.util.Date

data class StartWorkflowForm(
    val workflowId: Long = -1,
    val title: String = "",
    val startNow: Boolean = true,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    val approverUserId: Long? = null,
    val parameters: Map<String, String> = emptyMap(),
    val participants: List<Participant> = emptyList(),
)
