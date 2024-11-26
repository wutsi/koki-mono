package com.wutsi.koki.form.event

data class ApprovalCompletedEvent(
    val tenantId: Long = -1,
    val approvalId: Long = -1,
    val activityInstanceId: String = "",
)
