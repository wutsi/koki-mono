package com.wutsi.koki.form.event

import com.wutsi.koki.workflow.dto.ApprovalStatus

data class ApprovalCompletedEvent(
    val tenantId: Long = -1,
    val approvalId: Long = -1,
    val activityInstanceId: String = "",
    val workflowInstanceId: String = "",
    val status: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis(),
)
